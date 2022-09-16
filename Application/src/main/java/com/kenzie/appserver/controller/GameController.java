package com.kenzie.appserver.controller;


import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.GameSummaryResponse;
import com.kenzie.appserver.controller.model.InvalidUserException;
import com.kenzie.appserver.controller.model.NoExistingGameSummaryException;
import com.kenzie.appserver.controller.model.UpdateSummaryRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.UserResponse;
import com.kenzie.appserver.service.GameSummaryService;
import com.kenzie.capstone.service.client.ApiGatewayException;
import com.kenzie.capstone.service.model.UserResponseLambda;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import com.kenzie.appserver.controller.model.CreateSummaryRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/game/wordle")
public class GameController {

    // game mapping always wordle at this time - future consideration for different games
    private final String GAME = "wordle";

    private GameSummaryService gameService;

    public GameController(GameSummaryService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameSummaryResponse> postNewSummary(@RequestBody CreateSummaryRequest createSummaryRequest) {

        // removed these checks because annotation prevent from being null
            // if (createSummaryRequest.getGame() == null) {
            //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Game");
            // }
            // if (createSummaryRequest.getSessionNumber() == null) {
            //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Game Session");
            // }
        // removed this check -> user validated in service -> if exception throw -> handle in the front end
            // if (createSummaryRequest.getUserId().length() == 0) {
            //     createSummaryRequest.setUserId(UUID.randomUUID().toString());
            // }
        if (createSummaryRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request to create a new game summary");
        }
        try {
            GameSummaryResponse response = gameService.addSummary(createSummaryRequest);
            // status code 201 if successful
            return ResponseEntity.created(
                    URI.create("/game/wordle/date" + response.getDate() + "/" + response.getUserId())).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed attempted to post a new game summary", e);
        }
    }

    @GetMapping("/{summaryDate}/{userId}")
    public ResponseEntity<GameSummaryResponse> findGameSummaryFromUser(
            @PathVariable("summaryDate") String summaryDate,
            @PathVariable("userId") String userId) {
        try {
            GameSummaryResponse gameSummaryResponse = gameService.getSummary(GAME, summaryDate, userId);
            return ResponseEntity.ok(gameSummaryResponse);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed attempted to post a new game summary", e);
        }
    }

    @PutMapping("/editSummary")
    public ResponseEntity<GameSummaryResponse> updateGameSummary(
            @RequestBody UpdateSummaryRequest updateSummaryRequest) {
        try {
            GameSummaryResponse gameSummaryResponse = gameService.updateSummary(updateSummaryRequest);
            return ResponseEntity.ok(gameSummaryResponse);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed attempted to post a new game summary", e);
        }
    }

    @DeleteMapping("/{summaryDate}/{userId}")
    public ResponseEntity deleteSummaryBySummaryId(
            @PathVariable("summaryDate") String summaryDate,
            @PathVariable("userId") String userId) {
        gameService.deleteSummary(summaryDate, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{summaryDate}/all")
    public ResponseEntity<List<GameSummaryResponse>> findAllSummariesForDate(
            @PathVariable("summaryDate") String summaryDate) {
        List<GameSummaryResponse> leaderboard = gameService.getAllSummariesForDate(summaryDate);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<List<GameSummaryResponse>> findAllSummariesForUser(@PathVariable("userId") String userId) {
        List<GameSummaryResponse> gameSummaryResponseList = gameService.getAllSummariesFromUser(userId);
        if (gameSummaryResponseList == null || gameSummaryResponseList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(gameSummaryResponseList);
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponse> addNewUser(@RequestBody UserCreateRequest userCreateRequest) {
        if (userCreateRequest.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty UserId");
        }
        // if no username is chosen, assign first 8 characters of the userId (8 is arbitrary length)
        if (userCreateRequest.getUserName() == null) {
            userCreateRequest.setUserName(userCreateRequest.getUserId().substring(0,8));
        }
        UserResponse userResponse = gameService.addNewUser(userCreateRequest);
        return ResponseEntity.created(URI.create("/users/" + userResponse.getUserId())).body(userResponse);
    }

    @GetMapping("/user/{userId}")  // not sure if this one is needed in the controller
    public ResponseEntity<UserResponse> findUser(@PathVariable("userId") String userId) {
        UserResponseLambda userResponse = gameService.verifyUser(userId);

        if (userResponse == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserResponse(userResponse));
    }

    @GetMapping("/{summaryDate}/{userId}/friends")
    public ResponseEntity<List<GameSummaryResponse>> findAllSummariesForUserFriends(
            @PathVariable("summaryDate") String summaryDate,
            @PathVariable("userId") String userId) {
        List<GameSummaryResponse> gameSummaryResponseList = gameService.getFriendSummaries(userId, summaryDate);
        if (gameSummaryResponseList == null || gameSummaryResponseList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(gameSummaryResponseList);
    }

    @PutMapping("/user/{userId}/friends/add/{friendId}")
    public ResponseEntity<UserResponse> addFriend(
            @PathVariable("userId") String userId,
            @PathVariable("friendId") String friendId) {
        UserResponse userResponse = gameService.addFriend(userId, friendId);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/user/{userId}/friends/remove/{friendId}")
    public ResponseEntity<UserResponse> removeFriend(
            @PathVariable("userId") String userId,
            @PathVariable("friendId") String friendId) {
        UserResponse userResponse = gameService.removeFriend(userId, friendId);
        return ResponseEntity.ok(userResponse);
    }


    // this will handle all ApiGatewayExceptions if they are caught within this class
    @ExceptionHandler({ApiGatewayException.class})
    public ResponseEntity<String> handleException(HttpServletRequest req, ApiGatewayException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getMessage());
    }
}
