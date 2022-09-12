package com.kenzie.appserver.controller;


import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.SummaryResponse;
import com.kenzie.appserver.controller.model.UpdateSummaryRequest;
import com.kenzie.appserver.service.GameSummaryService;
import com.kenzie.capstone.service.model.UserCreateRequest;
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

        if (createSummaryRequest.getGameName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Game");
        }

        // If the userId is an empty string, then provide random userId
        if (createSummaryRequest.getUserId().length() == 0) {
            createSummaryRequest.setUserId(UUID.randomUUID().toString());
        }

        SummaryResponse response = gameService.addSummary(createSummaryRequest);

        return ResponseEntity.created(URI.create("/customers/" + response.getGameId() + "/" + response.getUserId())).body(response);
    }

    @GetMapping("/{gameId}/{userId}")
    public ResponseEntity<SummaryResponse> getSummary(@PathVariable("gameId") String gameId,
                                                      @PathVariable("userId") String userId) {
        SummaryResponse summaryResponse = gameService.getSummary(gameId, userId);
        if (summaryResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summaryResponse);
    }

    @PostMapping // not sure about this request body -> may need to be changed to a spring based model for annotations
    public ResponseEntity<UserResponse> addNewUser(@RequestBody UserCreateRequest userCreateRequest) {
        return null;
    }

    @GetMapping  // not sure if this one is needed in the controller
    public ResponseEntity<UserResponse> findUser(@PathVariable("userId") String userId) {
        return null;
    }

    @PutMapping
    public ResponseEntity<SummaryResponse> updateGameSummary(@PathVariable("gameId") String gameId,
            @RequestBody UpdateSummaryRequest updateSummaryRequest) {
        return null;
    }

    @GetMapping  // not sure if this one is needed in the controller
    public ResponseEntity<List<SummaryResponse>> findAllSummaries() {
        return null;
    }

    @GetMapping  // not sure if this one is needed in the controller
    public ResponseEntity<List<SummaryResponse>> findSummariesById(@PathVariable("userId") String userId) {
        return null;
    }


}
