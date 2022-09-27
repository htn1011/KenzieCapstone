package com.kenzie.appserver.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
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
    // static final String game = "wordle";
    // static final String userId = mockNeat.strings().valStr();
    // static final String userName = mockNeat.strings().valStr();
    // static final String date = LocalDate.now().toString();
    // static final String sessionNumber = mockNeat.strings().valStr();
    // static final String results = mockNeat.strings().valStr();
    // static CreateSummaryRequest createSummaryRequest;
    // static UserResponse testUser;
    // static final String userIdDeleteTest = mockNeat.strings().valStr();
    // static final String userNameDeleteTest = mockNeat.strings().valStr();
    //
    // static UserResponse testUserDeleteSummary;

    private static final ObjectMapper mapper = new ObjectMapper();
    @BeforeAll
    public static void setup() {
        mapper.registerModule(new Jdk8Module());
        // testUser = gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));
        // testUserDeleteSummary = gameSummaryService.addNewUser(new UserCreateRequest(userIdDeleteTest, userNameDeleteTest));
        // createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);



    }

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
        String username = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(userId, username));

        String date = LocalDate.now().toString();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();

        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);

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
        String userName = mockNeat.strings().valStr();
        String date = LocalDate.now().toString();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        //
        gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));

        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);

        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);
        mvc.perform(post("/game/wordle")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createSummaryRequest)));

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
        String userName = mockNeat.strings().valStr();
        String date = LocalDate.now().toString();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();

        gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));

        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);

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
        String userName = mockNeat.strings().valStr();
        String date = LocalDate.now().toString();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();

        gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));


        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);

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
        String date = LocalDate.now().toString();

        String userId = mockNeat.strings().valStr();
        String userName = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));

        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);
        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);
            // added by KK, fix attempt
            // gameSummaryResponse.setDate(date);

        String userId1 = mockNeat.strings().valStr();
        String username1 = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(userId1, username1));
        String sessionNumber1 = mockNeat.strings().valStr();
        String results1 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest1 = new CreateSummaryRequest(game, userId1, sessionNumber1, results1);
        GameSummaryResponse gameSummaryResponse1 = gameSummaryService.addSummary(createSummaryRequest1);
            // added by KK, fix attempt
            // gameSummaryResponse1.setDate(date);

        String userId2 = mockNeat.strings().valStr();
        String username2 = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(userId2, username2));
        String sessionNumber2 = mockNeat.strings().valStr();
        String results2 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest2 = new CreateSummaryRequest(game, userId2, sessionNumber2, results2);
        GameSummaryResponse gameSummaryResponse2 = gameSummaryService.addSummary(createSummaryRequest2);
            // added by KK, fix attempt
            // gameSummaryResponse2.setDate(date);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/{date}/all", date)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        List<GameSummaryResponse> responses = mapper.readValue(responseBody, new TypeReference<List<GameSummaryResponse>>() {});

        Assertions.assertThat(responses.size() > 0).as("There are several game summaries generated in test cycle");
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
        String userName = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));
        LocalDate today = LocalDate.now();

        String date = today.toString();
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, userId, sessionNumber, results);

        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);
        gameSummaryResponse.setDate(date);
        //
        // String date1 = today.minusDays(1L).toString();
        // System.out.println(date1);
        // String sessionNumber1 = mockNeat.strings().valStr();
        // String results1 = mockNeat.strings().valStr();
        // CreateSummaryRequest createSummaryRequest1 = new CreateSummaryRequest(game, userId, sessionNumber1, results1);
        //
        // GameSummaryResponse gameSummaryResponse1 = gameSummaryService.addSummary(createSummaryRequest1);
        // gameSummaryResponse1.setDate(date1);
        //
        // String date2 = today.minusDays(2L).toString();
        // System.out.println(date2);
        // String sessionNumber2 = mockNeat.strings().valStr();
        // String results2 = mockNeat.strings().valStr();
        // CreateSummaryRequest createSummaryRequest2 = new CreateSummaryRequest(game, userId, sessionNumber2, results2);
        //
        // GameSummaryResponse gameSummaryResponse2 = gameSummaryService.addSummary(createSummaryRequest2);
        // gameSummaryResponse2.setDate(date2);

        //WHEN
        ResultActions actions = mvc.perform(get("/game/wordle/user/{userId}/all", userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        String responseBody = actions.andReturn().getResponse().getContentAsString();

        List<GameSummaryResponse> responses = mapper.readValue(responseBody, new TypeReference<List<GameSummaryResponse>>() {});

        Assertions.assertThat(responses.size()).isEqualTo(1).as("There are 3 game summaries for userID");
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
        System.out.println("user: " + userId);
        // gameSummaryService.addNewUser(new UserCreateRequest(userId, userName));
        List<String> friendsList = new ArrayList<>();

        String game = "wordle";
        String date = LocalDate.now().toString();

        String friendId = mockNeat.strings().valStr();
        String friendName = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(friendId, friendName));
        String sessionNumber = mockNeat.strings().valStr();
        String results = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest = new CreateSummaryRequest(game, friendId, sessionNumber, results);
        GameSummaryResponse gameSummaryResponse = gameSummaryService.addSummary(createSummaryRequest);
        gameSummaryResponse.setDate(date);

        String friendId1 = mockNeat.strings().valStr();
        String friendName1 = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(friendId1, friendName1));
        String sessionNumber1 = mockNeat.strings().valStr();
        String results1 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest1 = new CreateSummaryRequest(game, friendId1, sessionNumber1, results1);
        GameSummaryResponse gameSummaryResponse1 = gameSummaryService.addSummary(createSummaryRequest1);
        gameSummaryResponse1.setDate(date);

        String friendId2 = mockNeat.strings().valStr();
        String friendName2 = mockNeat.strings().valStr();
        gameSummaryService.addNewUser(new UserCreateRequest(friendId2, friendName2));
        String sessionNumber2 = mockNeat.strings().valStr();
        String results2 = mockNeat.strings().valStr();
        CreateSummaryRequest createSummaryRequest2 = new CreateSummaryRequest(game, friendId2, sessionNumber2, results2);
        GameSummaryResponse gameSummaryResponse2 = gameSummaryService.addSummary(createSummaryRequest2);
        gameSummaryResponse2.setDate(date);



        UserCreateRequest userCreateRequest = new UserCreateRequest(userId, userName);
        UserResponse userResponse = gameSummaryService.addNewUser(userCreateRequest);



        // friendsList.add(friendId);
        // friendsList.add(friendId1);
        // friendsList.add(friendId2);
        // userResponse.setFriendsList(friendsList);

        // UserResponse userFriend = gameSummaryService.addFriend(userId, friendId);
        // System.out.println("userFriend: " + userFriend.getFriendsList());
        // UserResponse userFriend1 = gameSummaryService.addFriend(userId, friendId1);
        // System.out.println("userFriend1: " + userFriend1.getFriendsList());
        // UserResponse userFriend2 = gameSummaryService.addFriend(userId, friendId2);
        // System.out.println("userFriend2: " + userFriend2.getFriendsList());
        // UserResponseLambda user = gameSummaryService.verifyUser(userId);
        // System.out.println(user.getFriendsList());
        mvc.perform(put("/game/wordle/user/{userId}/friends/add/{friendId}", userId, friendId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
        mvc.perform(put("/game/wordle/user/{userId}/friends/add/{friendId}", userId, friendId1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));
        mvc.perform(put("/game/wordle/user/{userId}/friends/add/{friendId}", userId, friendId2)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // ResultActions action = mvc.perform(get("/game/wordle/user/{userID}", userId)
        //         .accept(MediaType.APPLICATION_JSON)
        //         .contentType(MediaType.APPLICATION_JSON));
        // String Body = action.andReturn().getResponse().getContentAsString();
        // UserResponse responsew = mapper.readValue(Body, UserResponse.class);



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
        // UserResponse addedFriendResponse = gameSummaryService.addFriend(userId, friendId);

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
        Assertions.assertThat(response.getFriendsList().get(0)).isEqualTo(friendId).as("The friend matches");
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
        ResultActions actions = mvc.perform(put("/game/wordle/user/{userId}/friends/remove/{friendId}", userId, friendId)
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