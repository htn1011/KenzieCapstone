package com.kenzie.appserver.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.*;
import com.kenzie.appserver.service.ExampleService;
import com.kenzie.appserver.service.GameSummaryService;
import com.kenzie.appserver.service.model.Example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.andreinc.mockneat.MockNeat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IntegrationTest
public class GameSummaryControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    GameSummaryService gameSummaryService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();

    // @Test
    // public void getById_Exists() throws Exception {
    //
    //     String name = mockNeat.strings().valStr();
    //
    //     GameSummary persistedGameSummary = exampleService.addNewExample(name);
    //     mvc.perform(get("/example/{id}", persistedGameSummary.getId())
    //                     .accept(MediaType.APPLICATION_JSON))
    //             .andExpect(jsonPath("id")
    //                     .isString())
    //             .andExpect(jsonPath("name")
    //                     .value(is(name)))
    //             .andExpect(status().is2xxSuccessful());
    // }

    // @Test
    // public void createExample_CreateSuccessful() throws Exception {
    //     String name = mockNeat.strings().valStr();
    //
    //     ExampleCreateRequest exampleCreateRequest = new ExampleCreateRequest();
    //     exampleCreateRequest.setName(name);
    //
    //     mapper.registerModule(new JavaTimeModule());
    //
    //     mvc.perform(post("/example")
    //                     .accept(MediaType.APPLICATION_JSON)
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(mapper.writeValueAsString(exampleCreateRequest)))
    //             .andExpect(jsonPath("id")
    //                     .exists())
    //             .andExpect(jsonPath("name")
    //                     .value(is(name)))
    //             .andExpect(status().is2xxSuccessful());
    // }

    @Test
    public void postNewSummary_success() throws Exception {
        //GIVEN
        String game = "wordle";
        String userId = mockNeat.strings().valStr();
        String date = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();

        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);

//        mapper.registerModule(new JavaTimeModule());

        //WHEN
        mvc.perform(post("/game/wordle")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createSummaryRequest)))
                // THEN
                .andExpect(jsonPath("game")
                        .exists())
                .andExpect(jsonPath("userId")
                        .value(is(userId)))
                .andExpect(jsonPath("date")
                        .value(is(date)))
                .andExpect(jsonPath("sessionNumber")
                        .value(is(sessionNumber)))
                .andExpect(jsonPath("results")
                        .value(is(results)))
                .andExpect(status().isCreated());
    }

    @Test
    public void findGameSummaryFromUser_gameSummaryExists() throws Exception {
        //GIVEN
        String game = "wordle";
        String userId = mockNeat.strings().valStr();
        String date = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();

        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);

        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/{summaryDate}/{userId}", date, userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        GameSummaryResponse response = mapper.readValue(responseBody, GameSummaryResponse.class);

        Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
        Assertions.assertThat(response.getGame()).isNotEmpty().as("The game is populated");
        Assertions.assertThat(response.getDate()).isNotEmpty().as("The date is populated");
        Assertions.assertThat(response.getSessionNumber()).isNotEmpty().as("The session number is populated");
        Assertions.assertThat(response.getResults()).isNotEmpty().as("The results is populated");
    }

    @Test
    public void updateGameSummary_success() throws Exception {
        //GIVEN
        String game = "wordle";
        String userId = mockNeat.strings().valStr();
        String date = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);

        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        String updatedResults = mockNeat.strings().valStr();
        UpdateSummaryRequest updateSummaryRequest = new UpdateSummaryRequest(date, userId, date, updatedResults);

        //WHEN
        ResultActions actions = mvc.perform(put("/game/wordle/editSummary")
                        .content(mapper.writeValueAsString(updateSummaryRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        GameSummaryResponse response = mapper.readValue(responseBody, GameSummaryResponse.class);
        Assertions.assertThat(response.getGame()).isNotEmpty().as("The game is populated");
        Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
        Assertions.assertThat(response.getDate()).isNotEmpty().as("The date is populated");
        Assertions.assertThat(response.getSessionNumber()).isNotEmpty().as("The sessionNumber is populated");
        Assertions.assertThat(response.getResults()).isEqualTo(updateSummaryRequest.getUpdatedResults()).as("The updated result is correct");
    }

    @Test
    public void deleteSummary_success() throws Exception {
        //GIVEN
        String game = "wordle";
        String userId = mockNeat.strings().valStr();
        String date = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();

        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);

        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        //WHEN
        mvc.perform(delete("/game/wordle/{date}/{userId}", gameSummaryResponse.getDate(), gameSummaryResponse.getUserId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        //THEN
        mvc.perform(get("/game/wordle/{date}/{userId}", date, userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findAllSummariesForDate_success() throws Exception {
        String game = "wordle";
        String date = mockNeat.strings().valStr();

        String userId = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);
        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        String userId1 = mockNeat.strings().valStr();
        String sessionNumber1 = mockNeat.strings().valStr();
        String results1 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest1 = new CreateSummaryRequest(game, userId1, date, sessionNumber1, results1);
        GameSummaryResponse gameSummaryResponse1 = gameSummaryService.addSummary(createSummaryRequest1);

        String userId2 = mockNeat.strings().valStr();
        String sessionNumber2 = mockNeat.strings().valStr();
        String results2 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest2 = new CreateSummaryRequest(game, userId2, date, sessionNumber2, results2);
        GameSummaryResponse gameSummaryResponse2 = gameSummaryService.addSummary(createSummaryRequest2);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/{date}/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        List<GameSummaryResponse> responses = mapper.readValue(responseBody, new TypeReference<List<GameSummaryResponse>>() {});

        Assertions.assertThat(responses.size()).isEqualTo(3).as("There are 3 game summaries");
        for (GameSummaryResponse response : responses) {
            Assertions.assertThat(response.getGame()).isNotEmpty().as("The game is populated");
            Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
            Assertions.assertThat(response.getDate()).isNotEmpty().as("The date is populated");
            Assertions.assertThat(response.getSessionNumber()).isNotEmpty().as("The sessionNumber is populated");
            Assertions.assertThat(response.getResults()).isNotEmpty().as("The results is populated");
        }
    }

    @Test
    public void findAllSummariesForUser_success() throws Exception {
        //GIVEN
        String game = "wordle";
        String userId = mockNeat.strings().valStr();

        String date = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, date, sessionNumber, results);

        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        String date1 = mockNeat.strings().valStr();
        String sessionNumber1 = mockNeat.strings().valStr();
        String results1 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest1 = new CreateSummaryRequest(game, userId, date1, sessionNumber1, results1);

        GameSummaryResponse gameSummaryResponse1 = gameSummaryService.addSummary(createSummaryRequest1);

        String date2 = mockNeat.strings().valStr();
        String sessionNumber2 = mockNeat.strings().valStr();
        String results2 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest2 = new CreateSummaryRequest(game, userId, date2, sessionNumber2, results2);

        GameSummaryResponse gameSummaryResponse2 = gameSummaryService.addSummary(createSummaryRequest2);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/{userId}/all", userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        List<GameSummaryResponse> responses = mapper.readValue(responseBody, new TypeReference<List<GameSummaryResponse>>() {});

        Assertions.assertThat(responses.size()).isEqualTo(3).as("There are 3 game summaries for userID");
        for (GameSummaryResponse response : responses) {
            Assertions.assertThat(response.getGame()).isNotEmpty().as("The game is populated");
            Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
            Assertions.assertThat(response.getDate()).isNotEmpty().as("The date is populated");
            Assertions.assertThat(response.getSessionNumber()).isNotEmpty().as("The sessionNumber is populated");
            Assertions.assertThat(response.getResults()).isNotEmpty().as("The results is populated");
        }
    }

    @Test
    public void addNewUser() throws Exception {
        //GIVEN
        String userId = mockNeat.strings().valStr();
        String userName = mockNeat.strings().valStr();
        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);

//        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);

        //WHEN
        ResultActions actions = mvc.perform(post("/game/wordle/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateRequest)))
                .andExpect(status().is2xxSuccessful());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        UserResponse response = mapper.readValue(responseBody, UserResponse.class);

        Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
        Assertions.assertThat(response.getUserName()).isNotEmpty().as("The userName is populated");
        Assertions.assertThat(response.getFriendsList()).isNullOrEmpty();

    }

    @Test
    public void findUser_success() throws Exception {
        //GIVEN
        String userId = mockNeat.strings().valStr();
        String userName = mockNeat.strings().valStr();

        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);

        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/user/{userID}", userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        UserResponse response = mapper.readValue(responseBody, UserResponse.class);

        Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
        Assertions.assertThat(response.getUserName()).isNotEmpty().as("The userName is populated");
        Assertions.assertThat(response.getFriendsList()).isNullOrEmpty();
    }

    @Test
    public void findAllSummariesForUserFriends_success() throws Exception {
        //GIVEN
        String userId = mockNeat.strings().valStr();
        String userName = mockNeat.strings().valStr();
        List<String> friendsList = new ArrayList<>();

        String game = "wordle";
        String date = mockNeat.strings().valStr();

        String friendId = mockNeat.strings().valStr();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, friendId, date, sessionNumber, results);
        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);

        String friendId1 = mockNeat.strings().valStr();
        String sessionNumber1 = mockNeat.strings().valStr();
        String results1 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest1 = new CreateSummaryRequest(game, friendId1, date, sessionNumber1, results1);
        GameSummaryResponse gameSummaryResponse1 = gameSummaryService.addSummary(createSummaryRequest1);

        String friendId2 = mockNeat.strings().valStr();
        String sessionNumber2 = mockNeat.strings().valStr();
        String results2 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest2 = new CreateSummaryRequest(game, friendId2, date, sessionNumber2, results2);
        GameSummaryResponse gameSummaryResponse2 = gameSummaryService.addSummary(createSummaryRequest2);

        friendsList.add(friendId);
        friendsList.add(friendId1);
        friendsList.add(friendId2);
        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);
        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/{date}/{userId}/friends", date, userId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        List<GameSummaryResponse> responses = mapper.readValue(responseBody, new TypeReference<List<GameSummaryResponse>>() {});
        Assertions.assertThat(responses.size()).isEqualTo(3).as("There are 3 game summaries");
        for (GameSummaryResponse response : responses) {
            Assertions.assertThat(response.getGame()).isNotEmpty().as("The game is populated");
            Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
            Assertions.assertThat(response.getDate()).isNotEmpty().as("The date is populated");
            Assertions.assertThat(response.getSessionNumber()).isNotEmpty().as("The sessionNumber is populated");
            Assertions.assertThat(response.getResults()).isNotEmpty().as("The results is populated");
        }
    }

    @Test
    public void addFriend_success() throws Exception {
        //GIVEN
        String friendId = mockNeat.strings().valStr();
        String friendUserName = mockNeat.strings().valStr();
        UserCreateRequest friendCreateRequest = new UserCreateRequest(friendId, friendUserName);
        UserResponse friendResponse = gameSummaryService.addNewUser(friendCreateRequest);

        String userId = mockNeat.strings().valStr();
        String userName = mockNeat.strings().valStr();
        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);
        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);
        UserResponse addedFriendResponse = gameSummaryService.addFriend(userId, friendId);

        //WHEN
        ResultActions actions = mvc.perform(put("/game/wordle/user/{userId}/friends/add/{friendId}", userId, friendId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        UserResponse response = mapper.readValue(responseBody, UserResponse.class);
        Assertions.assertThat(response.getUserId()).isNotEmpty().as("The userId is populated");
        Assertions.assertThat(response.getUserName()).isNotEmpty().as("The userName is populated");
        Assertions.assertThat(response.getFriendsList().get(0)).isEqualTo(addedFriendResponse.getFriendsList().get(0)).as("The friend matches");
    }

    @Test
    public void removeFriend_success() throws Exception {
        //GIVEN
        String friendId = mockNeat.strings().valStr();
        String friendUserName = mockNeat.strings().valStr();
        UserCreateRequest friendCreateRequest = new UserCreateRequest(friendId, friendUserName);
        UserResponse friendResponse = gameSummaryService.addNewUser(friendCreateRequest);

        String userId = mockNeat.strings().valStr();
        String userName = mockNeat.strings().valStr();
        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);
        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);
        userResponse = gameSummaryService.addFriend(userId, friendId);

        //WHEN
        ResultActions actions = mvc.perform(delete("/game/wordle/user/{userId}/friends/remove/{friendId}", userId, friendId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        //THEN
        mvc.perform(get("/game/wordle/user/{userId}/friends/{friendId}", userId, friendId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());



    }

}