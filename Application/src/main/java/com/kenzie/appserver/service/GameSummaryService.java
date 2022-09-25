package com.kenzie.appserver.service;

import com.kenzie.appserver.config.CacheStore;
import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.GameSummaryResponse;
import com.kenzie.appserver.controller.model.InvalidUserException;
import com.kenzie.appserver.controller.model.NoExistingGameSummaryException;
import com.kenzie.appserver.controller.model.UpdateSummaryRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.UserResponse;
import com.kenzie.appserver.repositories.GameRepository;
import com.kenzie.appserver.repositories.model.GameSummaryId;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.appserver.service.conversion.GameSummaryConversion;
import com.kenzie.capstone.service.client.ApiGatewayException;
import com.kenzie.capstone.service.client.UserServiceClient;
import com.kenzie.capstone.service.model.UserCreateRequestLambda;
import com.kenzie.capstone.service.model.UserResponseLambda;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameSummaryService {
    private static final String GAME = "wordle";
    private GameRepository gameRepository;
    private UserServiceClient userServiceClient;
    private CacheStore cache;

    public GameSummaryService(GameRepository gameRepository, UserServiceClient userServiceClient, CacheStore cache) {
        this.gameRepository = gameRepository;
        this.userServiceClient = userServiceClient;
        this.cache = cache;
    }

    public GameSummaryResponse addSummary(CreateSummaryRequest summaryRequest) {
        // use the userService client to verify the user will throw exception if not found
        verifyUser(summaryRequest.getUserId());
        //create the new record
        GameSummaryRecord record = GameSummaryConversion.createRequestToRecord(summaryRequest);
        // save it
        gameRepository.save(record);
        // invalidate the cache
        invalidateCache(record.getDate(), record.getUserId());
        // return it
        return GameSummaryConversion.recordToResponse(record);
    }

    public GameSummaryResponse getSummary(String game, String date, String userId) {
        // verify the user -> throw exception if invalid
        verifyUser(userId);
        // attempt to get the requested summary, throw an exception if it doesn't exist to be handled in controller
        return gameRepository.findById(generateGameSummaryID(date, userId))
                .map(GameSummaryConversion::recordToResponse)
                .orElseThrow(() -> new NoExistingGameSummaryException(game, date, userId));
    }

    public GameSummaryResponse updateSummary(UpdateSummaryRequest updateSummaryRequest) {
        // verify user
        verifyUser(updateSummaryRequest.getUserId());
        // get existing game summary
        GameSummaryRecord existingRecord = gameRepository.findById(
                generateGameSummaryID(
                        updateSummaryRequest.getExistingSummaryDate(),
                        updateSummaryRequest.getUserId()))
                .orElseThrow(() -> new NoExistingGameSummaryException(
                        updateSummaryRequest.getGame(),
                        updateSummaryRequest.getExistingSummaryDate(),
                        updateSummaryRequest.getUserId()));
        // update with new results
        existingRecord.setResults(updateSummaryRequest.getUpdatedResults());
        // save updated record to DB
        gameRepository.save(existingRecord);
        // invalidate all caches
        invalidateCache(existingRecord.getDate(), existingRecord.getUserId());
        // return updated record
        return GameSummaryConversion.recordToResponse(existingRecord);
    }

    public void deleteSummary(String date, String userId) {
        invalidateCache(date, userId);
        gameRepository.deleteById(generateGameSummaryID(date, userId));
    }

    public List<GameSummaryResponse> getAllSummariesForDate(String date) {
        // check cache for summaries from that date
        List<GameSummaryResponse> summaryResponsesHit = cache.get(formatGameDateKey(date));
        // if cache miss
        if (summaryResponsesHit == null) {
            List<GameSummaryResponse> allSummariesAfterMiss = new ArrayList<>();
            // add to cache wordle::date + list of summaries for that date
            Optional.ofNullable(gameRepository.findByDateOrderByResultsAsc(date))
                    .orElseGet(ArrayList::new)
                    .forEach(record -> allSummariesAfterMiss.add(GameSummaryConversion.recordToResponse(record)));
            cache.add(formatGameDateKey(date), allSummariesAfterMiss);
            return allSummariesAfterMiss;
        }
        return summaryResponsesHit;
    }

    public List<GameSummaryResponse> getAllSummariesFromUser(String userId) {
        // check cache for all summaries from a user
        List<GameSummaryResponse> summaryResponsesHit = cache.get(formatGameUserIdKey(userId));
        // if null - get from repo
        if (summaryResponsesHit == null) {
            List<GameSummaryResponse> summaryResponsesMiss =
                    // if the user had no records - return empty list
                    Optional.ofNullable(gameRepository.findByUserId(userId))
                            .orElseGet(ArrayList::new)
                    .stream()
                    .map(GameSummaryConversion::recordToResponse)
                    .collect(Collectors.toList());
        // add to cache wordle::user + list of that user's summaries
            cache.add(formatGameUserIdKey(userId), summaryResponsesMiss);
            return summaryResponsesMiss;
        }
        return summaryResponsesHit;
    }

    public List<GameSummaryResponse> getFriendSummaries(String userId, String date) {
        Set<String> friendsList = new HashSet<>(userServiceClient.getFriendList(userId));
        return Optional.ofNullable(cache.get(formatGameDateKey(date)))
                .orElseGet(() -> getAllSummariesForDate(date))
                .stream()
                .filter(response -> friendsList.contains(response.getUserId()))
                .sorted(Comparator.comparing(GameSummaryResponse::getResults))
                .collect(Collectors.toList());
    }

    public UserResponse addFriend(String userId, String friendId) {
        return new UserResponse(userServiceClient.addFriend(userId, friendId));
    }

    public UserResponse removeFriend(String userId, String friendId) {
        return new UserResponse(userServiceClient.removeFriend(userId, friendId));
    }

    public UserResponse addNewUser(UserCreateRequest userCreateRequest) {
        UserCreateRequestLambda userCreateRequestLambda = new UserCreateRequestLambda(
                userCreateRequest.getUserId(),
                userCreateRequest.getusername());
        return new UserResponse(userServiceClient.addNewUser(userCreateRequestLambda));
    }

    public UserResponseLambda verifyUser(String userId) {
        UserResponseLambda existingUser;
        try {
            // either the user exists and is returned
            existingUser = userServiceClient.findExistingUser(userId);
        } catch (ApiGatewayException e) {
            // or an exception is thrown and the exception should contain details to be able to debug
            // that or use effectively
            if (e.getStatusCode() == 404) {
                throw new InvalidUserException(userId, e);
            } else {
                throw e;
            }
        }
        return existingUser;
    }
    private String formatGameDateKey(String date) {

        return String.format("%s::%s", GAME, date);
    }

    private String formatGameUserIdKey(String userId) {

        return String.format("%s::%s", GAME, userId);
    }

    private GameSummaryId generateGameSummaryID(String date, String userId) {
        return new GameSummaryId(userId, formatGameDateKey(date));
    }
    private void invalidateCache(String date, String userId) {
        cache.evict(formatGameDateKey(date));
        cache.evict(formatGameUserIdKey(userId));
    }
}
