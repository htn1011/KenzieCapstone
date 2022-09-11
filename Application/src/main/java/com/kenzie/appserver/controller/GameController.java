package com.kenzie.appserver.controller;


import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.SummaryResponse;
import com.kenzie.appserver.service.GameSummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import com.kenzie.appserver.controller.model.CreateSummaryRequest;

import java.net.URI;
import java.util.Optional;
import java.util.Random;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid GameId");
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

}
