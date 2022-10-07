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
import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.capstone.service.client.ApiGatewayException;
import com.kenzie.capstone.service.client.UserServiceClient;
import com.kenzie.capstone.service.model.UserCreateRequestLambda;
import com.kenzie.capstone.service.model.UserResponseLambda;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameSummaryServiceTest {
    private GameRepository gameRepository;
    private GameSummaryService gameSummaryService;
    private UserServiceClient userServiceClient;
    private CacheStore cache;

    @BeforeEach
    void setup() {
        gameRepository = mock(GameRepository.class);
        userServiceClient = mock(UserServiceClient.class);
        cache = mock(CacheStore.class);
        gameSummaryService = new GameSummaryService(gameRepository, userServiceClient, cache);
    }

    @Test
    void addSummaryTest() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String sessionNumber = "sessionNumber";
        String results = "results";
        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);
        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        ArgumentCaptor<GameSummaryRecord> gameSummaryRecordArgumentCaptor =
                ArgumentCaptor.forClass(GameSummaryRecord.class);

        //WHEN
        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        //THEN
        Assertions.assertNotNull(gameSummaryResponse);
        verify(gameRepository).save(gameSummaryRecordArgumentCaptor.capture());
        verify(cache, times(2)).evict(any());

        GameSummaryRecord gameSummaryRecord = gameSummaryRecordArgumentCaptor.getValue();

        Assertions.assertNotNull(gameSummaryRecord, "Game summary record is returned");
        Assertions.assertNotNull(gameSummaryRecord.getUserId(), "The userid exists");
        Assertions.assertNotNull(gameSummaryRecord.getDate(), "The date exists");
        Assertions.assertNotNull(gameSummaryRecord.getGame(), "The game exists");
        Assertions.assertNotNull(gameSummaryRecord.getResults(), "The results exists");
        Assertions.assertNotNull(gameSummaryRecord.getSessionNumber(), "The session number exists");
    }

    @Test
    void addSummaryTest_userDoesNotExist_throwException() {
        //GIVEN
        String userId = "userId";
        String game = "wordle";
        String results = "results";
        String sessionNumber = "sessionNumber";
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);
        when(userServiceClient.findExistingUser(userId)).thenThrow(new InvalidUserException(userId));

        //WHEN
        Assertions.assertThrows(
                InvalidUserException.class,
                () -> gameSummaryService.addSummary(createSummaryRequest),
                "Userid does not exist");
    }

    @Test
    void getSummaryTest() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String sessionNumber = "sessionNumber";
        String results = "results";
        GameSummaryRecord gameSummaryRecord = new GameSummaryRecord(userId, game, date, results, sessionNumber);

        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);

        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        when(gameRepository.findByGameSummaryId(any())).thenReturn(Optional.of(gameSummaryRecord));

        //WHEN
        GameSummaryResponse gameSummaryResponse = gameSummaryService.getSummary(game, date, userId);

        //THEN
        Assertions.assertNotNull(gameSummaryResponse, "Response is returned");
        Assertions.assertEquals(gameSummaryRecord.getUserId(), gameSummaryResponse.getUserId(), "Userid matches");
        Assertions.assertEquals(gameSummaryRecord.getGame(), gameSummaryResponse.getGame(), "Game matches");
        Assertions.assertEquals(gameSummaryRecord.getDate(), gameSummaryResponse.getDate(), "Date matches");
        Assertions.assertEquals(gameSummaryRecord.getResults(), gameSummaryResponse.getResults(), "Results matches");
        Assertions.assertEquals(
                gameSummaryRecord.getSessionNumber(),
                gameSummaryResponse.getSessionNumber(),
                "Session number matches");
    }

    @Test
    void getSummary_userIdIsInvalid_throwsException() {
        //GIVEN
        String userId = "userId";
        String game = "wordle";
        String date = "date";
        when(userServiceClient.findExistingUser(userId)).thenThrow(new InvalidUserException(userId));

        //THEN
        Assertions.assertThrows(
                InvalidUserException.class,
                () -> gameSummaryService.getSummary(game, date, userId),
                "User does not exist");
    }

    @Test
    void getSummary_gameSummaryDoesNotExist_throwsException() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String userName = "userName";
        List<String> friendsList = new ArrayList<>();

        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);
        userResponseLambda.setUserName(userName);
        userResponseLambda.setFriendsList(friendsList);

        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        when(gameRepository.findByGameSummaryId(any())).thenReturn(Optional.empty());

        //THEN
        Assertions.assertThrows(
                NoExistingGameSummaryException.class,
                () -> gameSummaryService.getSummary(game, date, userId),
                "Game summary does not exist throws exception");
    }

    @Test
    void updateSummaryTest() {
        //GIVEN
        String date = "date";
        String userId = "userId";
        String game = "wordle";
        String results = "results";
        String sessionNumber = "sessionNumber";
        String updatedResults = "updatedResults";
        UpdateSummaryRequest updateSummaryRequest = new UpdateSummaryRequest(date, userId, game, updatedResults);
        GameSummaryRecord gameSummaryRecord = new GameSummaryRecord(userId, game, date, results, sessionNumber);
        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);
        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        when(gameRepository.findByGameSummaryId(any())).thenReturn(Optional.of(gameSummaryRecord));
        ArgumentCaptor<GameSummaryRecord> gameSummaryRecordArgumentCaptor =
                ArgumentCaptor.forClass(GameSummaryRecord.class);

        //WHEN
        GameSummaryResponse gameSummaryResponse = gameSummaryService.updateSummary(updateSummaryRequest);

        //THEN
        Assertions.assertNotNull(gameSummaryResponse);
        verify(gameRepository).save(gameSummaryRecordArgumentCaptor.capture());
        verify(cache, times(2)).evict(any());

        GameSummaryRecord gameSummaryRecord1 = gameSummaryRecordArgumentCaptor.getValue();

        Assertions.assertNotNull(gameSummaryRecord1, "Record is returned");
        Assertions.assertNotNull(gameSummaryRecord1.getUserId(), "Userid exists");
        Assertions.assertNotNull(gameSummaryRecord1.getGame(), "Game exists");
        Assertions.assertNotNull(gameSummaryRecord1.getDate(), "Date exists");
        Assertions.assertNotNull(gameSummaryRecord1.getResults(), "Results exists");
        Assertions.assertNotNull(gameSummaryRecord1.getSessionNumber(), "Session number exists");
    }

    @Test
    void updateSummaryTest_gameSummaryInvalid_throwsException() {
        //GIVEN
        String date = "date";
        String userId = "userId";
        String game = "wordle";
        String updatedResults = "updatedResults";
        UpdateSummaryRequest updateSummaryRequest = new UpdateSummaryRequest(date, userId, game, updatedResults);
        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);
        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        when(gameRepository.findByGameSummaryId(any())).thenReturn(Optional.empty());

        //THEN
        Assertions.assertThrows(
                NoExistingGameSummaryException.class,
                () -> gameSummaryService.updateSummary(updateSummaryRequest),
                "Invalid game summary throws exception");

    }

    @Test
    void deleteSummaryTest() {
        //GIVEN
        String userId = "userId";
        String date = "date";

        //WHEN
        gameSummaryService.deleteSummary(date, userId);

        //THEN
        verify(gameRepository, times(1)).deleteById(any());
        verify(cache, times(2)).evict(any());
    }

    @Test
    void getAllSummariesForDateTest_cacheHit() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String results = "results";
        String sessionNumber = "sessionNumber";
        GameSummaryResponse gameSummaryResponseHit = new GameSummaryResponse(
                game,
                userId,
                date,
                sessionNumber,
                results);

        String userId1 = "userId1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";
        GameSummaryResponse gameSummaryResponseHit1 = new GameSummaryResponse(
                game,
                userId1,
                date,
                sessionNumber1,
                results1);

        List<GameSummaryResponse> gameSummaryResponseHitList = new ArrayList<>();
        gameSummaryResponseHitList.add(gameSummaryResponseHit);
        gameSummaryResponseHitList.add(gameSummaryResponseHit1);
        when(cache.get(any())).thenReturn(gameSummaryResponseHitList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesForDate(date);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(gameSummaryResponseList.size(), 2, "List should have 2 entries");
        Assertions.assertEquals(
                gameSummaryResponseHitList.get(0).getUserId(),
                gameSummaryResponseList.get(0).getUserId(),
                "Userid should match");
        Assertions.assertEquals(
                gameSummaryResponseHitList.get(0).getGame(),
                gameSummaryResponseList.get(0).getGame(),
                "Game should match");
        Assertions.assertEquals(
                gameSummaryResponseHitList.get(0).getDate(),
                gameSummaryResponseList.get(0).getDate(),
                "Date should match");
        Assertions.assertEquals(
                gameSummaryResponseHitList.get(0).getResults(),
                gameSummaryResponseList.get(0).getResults(),
                "Results should match");
        Assertions.assertEquals(
                gameSummaryResponseHitList.get(0).getSessionNumber(),
                gameSummaryResponseList.get(0).getSessionNumber(),
                "Session number should match");
        verify(cache, atLeastOnce()).get(any());
    }

    @Test
    void getAllSummariesForDateTest_cacheMiss() {
        //GIVEN
        String game = "wordle";
        String date = "date";

        String userId = "userId";
        String results = "results";
        String sessionNumber = "sessionNumber";

        String userId1 = "userId1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";

        GameSummaryRecord gameSummaryRecord = new GameSummaryRecord(userId, game, date, results, sessionNumber);
        GameSummaryRecord gameSummaryRecord1 = new GameSummaryRecord(userId1, game, date, results1, sessionNumber1);

        List<GameSummaryRecord> gameSummaryRecordList = new ArrayList<>();
        gameSummaryRecordList.add(gameSummaryRecord);
        gameSummaryRecordList.add(gameSummaryRecord1);

        when(cache.get(any())).thenReturn(null);
        when(gameRepository.findByDate(date)).thenReturn(gameSummaryRecordList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesForDate(date);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(gameSummaryResponseList.size(), 2, "List should have 2 entries");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getUserId(), userId, "Userid should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getGame(), game, "Game should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getDate(), date, "Date should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getResults(), results, "Results should match");
        Assertions.assertEquals(
                gameSummaryResponseList.get(0).getSessionNumber(),
                sessionNumber,
                "Session number should match");
        verify(cache, atLeastOnce()).add(any(), any());
    }

    @Test
    void getAllSummariesFromUserTest_cacheHit() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String results = "results";
        String sessionNumber = "sessionNumber";
        GameSummaryResponse gameSummaryResponseHit = new GameSummaryResponse(
                game,
                userId,
                date,
                sessionNumber,
                results);

        String date1 = "date1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";
        GameSummaryResponse gameSummaryResponseHit1 = new GameSummaryResponse(
                game,
                userId,
                date1,
                sessionNumber1,
                results1);

        List<GameSummaryResponse> gameSummaryResponseHitList = new ArrayList<>();
        gameSummaryResponseHitList.add(gameSummaryResponseHit);
        gameSummaryResponseHitList.add(gameSummaryResponseHit1);
        when(cache.get(any())).thenReturn(gameSummaryResponseHitList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesFromUser(userId);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(2, gameSummaryResponseList.size(), "List should have 2 entries");
        Assertions.assertEquals(
                gameSummaryResponseList.get(0).getUserId(),
                gameSummaryResponseHitList.get(0).getUserId(),
                "Userid should match");
        Assertions.assertEquals(
                gameSummaryResponseList.get(0).getGame(),
                gameSummaryResponseHitList.get(0).getGame(),
                "Game should match");
        Assertions.assertEquals(
                gameSummaryResponseList.get(0).getDate(),
                gameSummaryResponseHitList.get(0).getDate(),
                "Date should match");
        Assertions.assertEquals(
                gameSummaryResponseList.get(0).getResults(),
                gameSummaryResponseHitList.get(0).getResults(),
                "Results should match");
        Assertions.assertEquals(
                gameSummaryResponseList.get(0).getSessionNumber(),
                gameSummaryResponseHitList.get(0).getSessionNumber(),
                "Session number should match");
        verify(cache).get(any());
    }

    @Test
    void getAllSummariesFromUserTest_cacheMiss() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";

        String date = "date";
        String results = "results";
        String sessionNumber = "sessionNumber";

        String date1 = "date1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";

        GameSummaryRecord gameSummaryRecord = new GameSummaryRecord(userId, game, date, results, sessionNumber);
        GameSummaryRecord gameSummaryRecord1 = new GameSummaryRecord(userId, game, date1, results1, sessionNumber1);

        List<GameSummaryRecord> gameSummaryRecordList = new ArrayList<>();
        gameSummaryRecordList.add(gameSummaryRecord);
        gameSummaryRecordList.add(gameSummaryRecord1);

        when(cache.get(any())).thenReturn(null);
        when(gameRepository.findByUserId(userId)).thenReturn(gameSummaryRecordList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesFromUser(userId);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(2, gameSummaryResponseList.size(), "List should have 2 entries");
        Assertions.assertEquals(userId, gameSummaryResponseList.get(0).getUserId(), "Userid should match");
        Assertions.assertEquals(game, gameSummaryResponseList.get(0).getGame(), "Game should match");
        Assertions.assertEquals(date, gameSummaryResponseList.get(0).getDate(), "Date should match");
        Assertions.assertEquals(results, gameSummaryResponseList.get(0).getResults(), "Results should match");
        Assertions.assertEquals(
                sessionNumber,
                gameSummaryResponseList.get(0).getSessionNumber(),
                "Session number should match");
        verify(cache).add(any(), any());
    }

    @Test
    void getFriendSummaries() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";

        String friend = "friend";
        String results = "results";
        String sessionNumber = "sessionNumber";

        String friend1 = "friend1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";

        GameSummaryResponse gameSummaryResponse = new GameSummaryResponse(game, friend, date, sessionNumber, results);
        GameSummaryResponse gameSummaryResponse1 = new GameSummaryResponse(
                game,
                friend1,
                date,
                sessionNumber1,
                results1);
        List<GameSummaryResponse> gameSummaryResponseList = new ArrayList<>();
        gameSummaryResponseList.add(gameSummaryResponse);
        gameSummaryResponseList.add(gameSummaryResponse1);

        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);

        when(userServiceClient.getFriendList(userId)).thenReturn(friendsList);
        when(cache.get(any())).thenReturn(gameSummaryResponseList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList1 = gameSummaryService.getFriendSummaries(userId, date);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList1);
        Assertions.assertEquals(friend, gameSummaryResponseList.get(0).getUserId(), "Friend userid matches");
        Assertions.assertEquals(friend1, gameSummaryResponseList.get(1).getUserId(), "Friend userId matches");
        verify(cache).get(any());
    }

    @Test
    void addFriend() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        List<String> friendsList = new ArrayList<>();
        friendsList.add("friend1");
        friendsList.add("friend2");
        UserResponseLambda userResponseLambda = new UserResponseLambda(userId, userName, friendsList);
        String friendId = "friendId";
        friendsList.add(friendId);
        when(userServiceClient.addFriend(userId, friendId)).thenReturn(userResponseLambda);

        //WHEN
        UserResponse response = gameSummaryService.addFriend(userId, friendId);

        //THEN
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userResponseLambda.getUserId(), response.getUserId(), "Userid should match");
        Assertions.assertEquals(userResponseLambda.getUserName(), response.getUserName(), "Username should match");
        Assertions.assertEquals(userResponseLambda.getFriendsList(), response.getFriendsList(), "Friends list match");

    }

    @Test
    void removeFriend() {
        //GIVEN
        String friendId = "friendId";
        String userId = "userId";
        String userName = "user";
        List<String> friendsList = new ArrayList<>();
        UserResponseLambda userResponseLambda = new UserResponseLambda(userId, userName, friendsList);
        when(userServiceClient.removeFriend(userId, friendId)).thenReturn(userResponseLambda);

        //WHEN
        UserResponse removeFriendResponse = gameSummaryService.removeFriend(userId, friendId);

        //THEN
        Assertions.assertNotNull(removeFriendResponse, "Response is not null");
        Assertions.assertEquals(userId, removeFriendResponse.getUserId(), "Userid should match");
        Assertions.assertEquals(userName, removeFriendResponse.getUserName(), "Username should match");
        Assertions.assertFalse(
                removeFriendResponse.getFriendsList().contains(friendId),
                "Friend is not in friends list");
    }

    @Test
    void addNewUser() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);

        UserCreateRequestLambda userCreateRequestLambda = new UserCreateRequestLambda(
                userCreateRequest.getUserId(),
                userCreateRequest.getusername());

        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);
        userResponseLambda.setUserName(userName);
        when(userServiceClient.addNewUser(any())).thenReturn(userResponseLambda);

        //WHEN
        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);

        //THEN
        Assertions.assertNotNull(userResponse, "Response is not null");
        Assertions.assertEquals(userId, userResponse.getUserId(), "UserId matches");
        Assertions.assertEquals(userName, userResponse.getUserName(), "userName matches");
    }

    @Test
    void verifyUser() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        List<String> friendsList = new ArrayList<>();
        friendsList.add("friend");
        friendsList.add("friend1");
        UserResponseLambda userResponseLambda = new UserResponseLambda(userId, userName, friendsList);
        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);

        //WHEN
        UserResponseLambda response = gameSummaryService.verifyUser(userId);

        //THEN
        Assertions.assertNotNull(response, "Response is not null");
        Assertions.assertEquals(userId, response.getUserId(), "UserId matches");
        Assertions.assertEquals(userName, response.getUserName(), "Username matches");
        Assertions.assertEquals(friendsList, response.getFriendsList(), "Friends list matches");
        verify(userServiceClient, times(1)).findExistingUser(userId);
    }

    @Test
    void verifyUser_invalidUser_throwsException() {
        //GIVEN
        String userId = "";
        ApiGatewayException apiGatewayException = new ApiGatewayException("error");
        apiGatewayException.setStatusCode(404);
        when(userServiceClient.findExistingUser(userId)).thenThrow(apiGatewayException);

        //THEN
        Assertions.assertThrows(
                InvalidUserException.class,
                () -> gameSummaryService.verifyUser(userId),
                "Invalid userid throws exception");

    }
}
