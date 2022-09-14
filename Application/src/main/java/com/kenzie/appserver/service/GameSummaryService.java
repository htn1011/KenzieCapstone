package com.kenzie.appserver.service;

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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameSummaryService {
    private GameRepository gameRepository;
    private UserServiceClient userServiceClient;

    public GameSummaryService(GameRepository gameRepository, UserServiceClient userServiceClient) {
        this.gameRepository = gameRepository;
        this.userServiceClient = userServiceClient;
    }

    public GameSummaryResponse addSummary(CreateSummaryRequest summaryRequest) {
        // steps:
        // use the userService client to verify the user will throw exception if not found
        verifyUser(summaryRequest.getUserId());
        //create the new record
        GameSummaryRecord record = GameSummaryConversion.createRequestToRecord(summaryRequest);
        // save it
        gameRepository.save(record);
        // return it
        return GameSummaryConversion.recordToResponse(record);
    }

    public GameSummaryResponse getSummary(String game, String date, String userId) {
        // verify the user -> throw exception if invalid
        verifyUser(userId);
        // attempt to get the requested summary, throw an excpetion if it doesn't exist to be handled in controller
        return gameRepository.findById(generateGameSummary(game, date, userId))
                .map(GameSummaryConversion::recordToResponse)
                .orElseThrow(() -> new NoExistingGameSummaryException(game, date, userId));
    }

    public GameSummaryResponse updateSummary(UpdateSummaryRequest updateSummaryRequest) {
        // verify user
        verifyUser(updateSummaryRequest.getUserId());

        // get existing game summary
        GameSummaryRecord existingRecord = gameRepository.findById(
                generateGameSummary(
                        updateSummaryRequest.getGame(),
                        updateSummaryRequest.getExistingSummaryDate(),
                        updateSummaryRequest.getUserId()))
                .orElseThrow(() -> new NoExistingGameSummaryException(
                        updateSummaryRequest.getGame(),
                        updateSummaryRequest.getExistingSummaryDate(),
                        updateSummaryRequest.getUserId()));

        // update with new results
        existingRecord.setResults(updateSummaryRequest.getUpdatedResults());
        gameRepository.save(existingRecord);
        // save updated record to DB

        // return updated record
        return GameSummaryConversion.recordToResponse(existingRecord);
    }

    public void deleteSummary(String game, String date, String userId) {
        gameRepository.deleteById(generateGameSummary(game, date, userId));
    }

    public List<GameSummaryResponse> getAllSummariesForDate(String date) {
        List<GameSummaryResponse> allSummaries = new ArrayList<>();
        gameRepository.findByDate(date)
                .forEach(record -> allSummaries.add(GameSummaryConversion.recordToResponse(record)));
        return allSummaries;
    }

    public List<GameSummaryResponse> getAllSummariesFromUser(String userId) {
        return gameRepository.findByUserId(userId)
                .stream()
                .map(GameSummaryConversion::recordToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse addNewUser(UserCreateRequest userCreateRequest) {
        // request comes from the controller to add a new user
        // calls the user client
        // todo make sure the user doesn't already exist
        UserCreateRequestLambda userCreateRequestLambda = new UserCreateRequestLambda(
                userCreateRequest.getUserId(),
                userCreateRequest.getUserName());
        return new UserResponse(userServiceClient.addNewUser(userCreateRequestLambda));
    }

    public UserResponseLambda verifyUser(String userId) {
        UserResponseLambda existingUser;
        try {
            // either the user exists and is returned
            existingUser = userServiceClient.findExistingUser(userId);
        } catch (ApiGatewayException e) {
            // or an exception is thrown and the exception should contain details to be able to debug that or use effectively
            if (e.getStatusCode() == 404) {
                throw new InvalidUserException(userId, e);
            } else {
                throw e;
            }
        }
        return existingUser;
    }
    private String formatSummarySortKey(String game, String date) {

        return String.format("%s::%s", game, date);
    }

    private String formatIndexSortKey(String game, String userId) {

        return String.format("%s::%s", game, userId);
    }

    private GameSummaryId generateGameSummary(String game, String date, String userId) {
        return new GameSummaryId(userId, formatSummarySortKey(game, date));
    }
}
