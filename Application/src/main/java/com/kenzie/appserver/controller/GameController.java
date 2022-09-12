package com.kenzie.appserver.controller;


import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.SummaryResponse;
import com.kenzie.appserver.controller.model.UpdateSummaryRequest;
import com.kenzie.appserver.service.GameSummaryService;
import com.kenzie.capstone.service.model.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import com.kenzie.appserver.controller.model.CreateSummaryRequest;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private GameSummaryService gameService;

    public GameController(GameSummaryService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<SummaryResponse> postNewSummary(@RequestBody CreateSummaryRequest createSummaryRequest) {

        if (createSummaryRequest.getGameId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Game");
        }

        if (createSummaryRequest.getSessionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Game Session");
        }

        // If the userId is an empty string, then provide random userId
        if (createSummaryRequest.getUserId().length() == 0) {
            createSummaryRequest.setUserId(UUID.randomUUID().toString());
        }

        SummaryResponse response = gameService.addSummary(createSummaryRequest);

        // destruct summaryId into gameId and sessionId, use elsewhere
//        String[] splitSummaryId = response.getSummaryId().split("::");
//        String gameId = splitSummaryId[0];
//        String sessionId = splitSummaryId[1];

        return ResponseEntity.created(URI.create("/game/" + response.getSummaryId() + "/" + response.getUserId())).body(response);
    }

    @GetMapping("/{summaryId}/{userId}")
    public ResponseEntity<SummaryResponse> findGameSummaryFromUser(@PathVariable("summaryId") String summaryId,
                                                      @PathVariable("userId") String userId) {
        SummaryResponse summaryResponse = gameService.getSummary(summaryId, userId);
        if (summaryResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summaryResponse);
    }
    @PutMapping
    public ResponseEntity<SummaryResponse> updateGameSummary(@RequestBody UpdateSummaryRequest updateSummaryRequest) {

        SummaryResponse summaryResponse = gameService.updateSummary(updateSummaryRequest.getSummaryId(), updateSummaryRequest.getUserId());

        return ResponseEntity.ok(summaryResponse);;
    }

    @DeleteMapping("/{summaryId}/{userId}")
    public ResponseEntity deleteSummaryBySummaryId(@PathVariable("summaryId") String summaryId) {
        gameService.deleteSummary(summaryId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{summaryId}")  // not sure if this one is needed in the controller
    public ResponseEntity<List<SummaryResponse>> findAllSummaries() {
        List<SummaryResponse> leaderboard = customerService.getAllSummaries();

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{summaryId}/{userId}")  // not sure if this one is needed in the controller
    public ResponseEntity<List<SummaryResponse>> findAllSummariesFromUser(@PathVariable("userId") String userId) {
        List<SummaryResponse> summaryResponseList = gameService.getAllSummariesFromUser();  //@TODO implement in service
        if (summaryResponseList == null || summaryResponseList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(summaryResponseList);
    }

    @PostMapping // not sure about this request body -> may need to be changed to a spring based model for annotations
    public ResponseEntity<UserResponse> addNewUser(@RequestBody UserCreateRequest userCreateRequest) {
        if (userCreateRequest.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty UserId");
        }
        // if no username is chosen, assign first 8 characters of the userId (8 is arbitrary length)
        if (userCreateRequest.getUserName() == null) {
            userCreateRequest.setUserName(userCreateRequest.getUserId().substring(0,8));
        }

        UserResponse userResponse = gameService.addNewUser(userCreateRequest);
        return ResponseEntity.created(URI.create("/users/" + userResponse.getUserId())).body(userResponse);;
    }

    @GetMapping("/users/{userId}")  // not sure if this one is needed in the controller
    public ResponseEntity<UserResponse> findUser(@PathVariable("userId") String userId) {
        UserResponse userResponse = gameService.getUser(userId);

        if (userResponse == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userResponse);
    }




}
