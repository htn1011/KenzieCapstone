package com.kenzie.appserver.service;

import com.kenzie.appserver.config.CacheStore;
import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.GameSummaryResponse;
import com.kenzie.appserver.controller.model.NoExistingGameSummaryException;
import com.kenzie.appserver.controller.model.UpdateSummaryRequest;
import com.kenzie.appserver.repositories.GameRepository;
import com.kenzie.appserver.repositories.model.GameSummaryId;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.capstone.service.client.UserServiceClient;
import com.kenzie.capstone.service.model.NoExistingUserException;
import com.kenzie.capstone.service.model.UserResponseLambda;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.mockito.Mockito.*;

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
        // exampleService = new ExampleService(gameRepository, userServiceClient);
    }
    /** ------------------------------------------------------------------------
     *  exampleService.findById
     *  ------------------------------------------------------------------------ **/

    // @Test
    // void findById() {
    //     // GIVEN
    //     String id = randomUUID().toString();
    //
    //     GameSummaryRecord record = new GameSummaryRecord();
    //     record.setSummaryId(id);
    //     record.setName("concertname");
    //
    //     // WHEN
    //     when(gameRepository.findById(id)).thenReturn(Optional.of(record));
    //     Example example = exampleService.findById(id);
    //
    //     // THEN
    //     Assertions.assertNotNull(example, "The object is returned");
    //     Assertions.assertEquals(record.getSummaryId(), example.getId(), "The id matches");
    //     Assertions.assertEquals(record.getDate(), example.getName(), "The name matches");
    // }
    //
    // @Test
    // void findByConcertId_invalid() {
    //     // GIVEN
    //     String id = randomUUID().toString();
    //
    //     when(gameRepository.findById(id)).thenReturn(Optional.empty());
    //
    //     // WHEN
    //     Example example = exampleService.findById(id);
    //
    //     // THEN
    //     Assertions.assertNull(example, "The example is null when not found");
    // }

    @Test
    void addSummary_returnsResponse() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String sessionNumber = "sessionNumber";
        String results = "results";
        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);
        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        ArgumentCaptor<GameSummaryRecord> gameSummaryRecordArgumentCaptor = ArgumentCaptor.forClass(GameSummaryRecord.class);

        //WHEN
        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        //THEN
        Assertions.assertNotNull(gameSummaryResponse);
        verify(gameRepository).save(gameSummaryRecordArgumentCaptor.capture());
        verify(cache, times(2)).evict(any());

        GameSummaryRecord gameSummaryRecord = gameSummaryRecordArgumentCaptor.getValue();

        Assertions.assertNotNull(gameSummaryRecord, "Game summary record is returned");
        Assertions.assertNotNull(gameSummaryRecord.getUserId(), "The userId exists");
        Assertions.assertNotNull(gameSummaryRecord.getDate(), "The date exists");
        Assertions.assertNotNull(gameSummaryRecord.getGame(), "The game exists");
        Assertions.assertNotNull(gameSummaryRecord.getResults(), "The results exists");
        Assertions.assertNotNull(gameSummaryRecord.getSessionNumber(), "The session number exists");
    }

    @Test
    void addSummary_userDoesNotExist_throwException() {
        //GIVEN
        String userId = "userId";
        String game = "wordle";
        String date = "date";
        String results = "results";
        String sessionNumber = "sessionNumber";
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);
        doThrow(NoExistingUserException.class).when(userServiceClient).findExistingUser(userId);

        //WHEN
        Assertions.assertThrows(NoExistingUserException.class, () -> gameSummaryService.addSummary(createSummaryRequest), "UserId does not exist");

    }

    @Test
    void getSummary_returnsResponse() {
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
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameSummaryRecord));

        //WHEN
        GameSummaryResponse gameSummaryResponse = gameSummaryService.getSummary(game, date, userId);

        //THEN
        Assertions.assertNotNull(gameSummaryResponse, "Response is returned");
        Assertions.assertEquals(gameSummaryResponse.getUserId(), gameSummaryRecord.getUserId(), "UserId matches");
        Assertions.assertEquals(gameSummaryResponse.getGame(), gameSummaryRecord.getGame(), "Game matches");
        Assertions.assertEquals(gameSummaryResponse.getDate(), gameSummaryRecord.getDate(), "Date matches");
        Assertions.assertEquals(gameSummaryResponse.getResults(), gameSummaryRecord.getResults(), "Results matches");
        Assertions.assertEquals(gameSummaryResponse.getSessionNumber(), gameSummaryRecord.getSessionNumber(), "Session number matches");
    }

    @Test
    void getSummary_gameSummaryDoesNotExist_throwsException() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String sessionNumber = "sessionNumber";
        String results = "results";

        UserResponseLambda userResponseLambda = new UserResponseLambda();
        userResponseLambda.setUserId(userId);

        when(userServiceClient.findExistingUser(userId)).thenReturn(userResponseLambda);
        doThrow(NoExistingGameSummaryException.class).when(gameRepository).findById(any());

        //THEN
        Assertions.assertThrows(NoExistingGameSummaryException.class, () -> gameSummaryService.getSummary(game, date, userId), "Game summary does not exist");
    }

    @Test
    void updateSummary_returnsResponse() {
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
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameSummaryRecord));
        ArgumentCaptor<GameSummaryRecord> gameSummaryRecordArgumentCaptor = ArgumentCaptor.forClass(GameSummaryRecord.class);

        //WHEN
        GameSummaryResponse gameSummaryResponse = gameSummaryService.updateSummary(updateSummaryRequest);

        //THEN
        Assertions.assertNotNull(gameSummaryResponse);
        verify(gameRepository).save(gameSummaryRecordArgumentCaptor.capture());
        verify(cache, times(2)).evict(any());

        GameSummaryRecord gameSummaryRecord1 = gameSummaryRecordArgumentCaptor.getValue();

        Assertions.assertNotNull(gameSummaryRecord1, "Record is returned");
        Assertions.assertNotNull(gameSummaryRecord1.getUserId(), "UserId exists");
        Assertions.assertNotNull(gameSummaryRecord1.getGame(), "Game exists");
        Assertions.assertNotNull(gameSummaryRecord1.getDate(), "Date exists");
        Assertions.assertNotNull(gameSummaryRecord1.getResults(), "Results exists");
        Assertions.assertNotNull(gameSummaryRecord1.getSessionNumber(), "Session number exists");
    }

    @Test
    void deleteSummary_deletesRecord() {
        //GIVEN
        String userId = "userId";
        String date = "date";
        when(gameRepository.findByUserId(userId)).thenReturn(null);

        //WHEN
        gameSummaryService.deleteSummary(date, userId);

        //THEN
        verify(gameRepository, atLeastOnce()).deleteById(any());
        verify(cache, times(2)).evict(any());

        Assertions.assertNull(gameRepository.findByUserId(userId), "Returns null");
    }

    @Test
    void getAllSummariesForDate_cacheHit() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String results = "results";
        String sessionNumber = "sessionNumber";
        GameSummaryResponse gameSummaryResponseHit = new GameSummaryResponse(game, userId, date, sessionNumber, results);

        String userId1 = "userId1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";
        GameSummaryResponse gameSummaryResponseHit1 = new GameSummaryResponse(game, userId1, date, sessionNumber1, results1);

        List<GameSummaryResponse> gameSummaryResponseHitList = new ArrayList<>();
        gameSummaryResponseHitList.add(gameSummaryResponseHit);
        gameSummaryResponseHitList.add(gameSummaryResponseHit1);
        when(cache.get(any())).thenReturn(gameSummaryResponseHitList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesForDate(date);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(gameSummaryResponseList.size(), 2, "List should have 2 entries");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getUserId(), gameSummaryResponseHitList.get(0).getUserId(), "UserId should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getGame(), gameSummaryResponseHitList.get(0).getGame(), "Game should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getDate(), gameSummaryResponseHitList.get(0).getDate(), "Date should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getResults(), gameSummaryResponseHitList.get(0).getResults(), "Results should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getSessionNumber(), gameSummaryResponseHitList.get(0).getSessionNumber(), "Session number should match");

        verify(cache, atLeastOnce()).get(any());

    }
    @Test
    void getAllSummariesForDate_cacheMiss() {
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
        when(gameRepository.findByDateOrderByResultsAsc(date)).thenReturn(gameSummaryRecordList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesForDate(date);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(gameSummaryResponseList.size(), 2, "List should have 2 entries");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getUserId(), userId, "UserId should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getGame(), game, "Game should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getDate(), date, "Date should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getResults(), results, "Results should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getSessionNumber(), sessionNumber, "Session number should match");
        verify(cache, atLeastOnce()).add(any(), any());
    }

    @Test
    void getAllSummariesFromUser_cacheHit() {
        //GIVEN
        String game = "wordle";
        String userId = "userId";
        String date = "date";
        String results = "results";
        String sessionNumber = "sessionNumber";
        GameSummaryResponse gameSummaryResponseHit = new GameSummaryResponse(game, userId, date, sessionNumber, results);

        String date1 = "date1";
        String results1 = "results1";
        String sessionNumber1 = "sessionNumber1";
        GameSummaryResponse gameSummaryResponseHit1 = new GameSummaryResponse(game, userId, date1, sessionNumber1, results1);

        List<GameSummaryResponse> gameSummaryResponseHitList = new ArrayList<>();
        gameSummaryResponseHitList.add(gameSummaryResponseHit);
        gameSummaryResponseHitList.add(gameSummaryResponseHit1);
        when(cache.get(any())).thenReturn(gameSummaryResponseHitList);

        //WHEN
        List<GameSummaryResponse> gameSummaryResponseList = gameSummaryService.getAllSummariesFromUser(userId);

        //THEN
        Assertions.assertNotNull(gameSummaryResponseList);
        Assertions.assertEquals(gameSummaryResponseList.size(), 2, "List should have 2 entries");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getUserId(), gameSummaryResponseHitList.get(0).getUserId(), "UserId should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getGame(), gameSummaryResponseHitList.get(0).getGame(), "Game should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getDate(), gameSummaryResponseHitList.get(0).getDate(), "Date should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getResults(), gameSummaryResponseHitList.get(0).getResults(), "Results should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getSessionNumber(), gameSummaryResponseHitList.get(0).getSessionNumber(), "Session number should match");
        verify(cache, atLeastOnce()).get(any());
    }

    @Test
    void getAllSummariesFromUser_cacheMiss() {
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
        Assertions.assertEquals(gameSummaryResponseList.size(), 2, "List should have 2 entries");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getUserId(), userId, "UserId should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getGame(), game, "Game should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getDate(), date, "Date should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getResults(), results, "Results should match");
        Assertions.assertEquals(gameSummaryResponseList.get(0).getSessionNumber(), sessionNumber, "Session number should match");
        verify(cache, atLeastOnce()).add(any(), any());
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
        GameSummaryResponse gameSummaryResponse1 = new GameSummaryResponse(game, friend1, date, sessionNumber1, results1);
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
        Assertions.assertEquals(gameSummaryResponseList.get(0).getUserId(), friend, "UserId matches");
        Assertions.assertEquals(gameSummaryResponseList.get(1).getUserId(), friend1, "UserId matches");
    }

    @Test
    void addNewFriend() {
        //Implement
    }

    @Test
    void removeFriend() {
        //Implement
    }

    @Test
    void addNewUser() {
        //Implement
    }

    @Test
    void verifyUser() {
        //Implement
    }
}
