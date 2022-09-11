package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.SummaryResponse;
import com.kenzie.appserver.repositories.GameRepository;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.appserver.service.conversion.GameSummaryConversion;
import com.kenzie.appserver.service.model.Example;
import com.kenzie.capstone.service.client.UserServiceClient;
import com.kenzie.capstone.service.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public class GameSummaryService {
    private GameRepository gameRepository;
    private UserServiceClient userServiceClient;

    public GameSummaryService(GameRepository gameRepository, UserServiceClient userServiceClient) {
        this.gameRepository = gameRepository;
        this.userServiceClient = userServiceClient;
    }

    public SummaryResponse addSummary(CreateSummaryRequest summaryRequest) {
        User dataFromLambda = userServiceClient.setExampleData(summaryRequest.getUserId());

        //if user is null, do we want to throw an exception here or handle it in the userServiceClient?

        GameSummaryRecord record = GameSummaryConversion.createRequestToRecord(summaryRequest);
        gameRepository.save(record);

        return GameSummaryConversion.recordToResponse(record);
    }

    public SummaryResponse getSummary(String gameId, String userId) {
        User dataFromLambda = userServiceClient.getExampleData(userId);

        // if user is null, how do we handle this? Is the exception thrown in the method for userServiceClient checking for null?

        Optional<GameSummaryRecord> record = gameRepository.findById(gameId);

        if (record.isEmpty()) {
            //how do we handle this?
        }

        return GameSummaryConversion.recordToResponse(record.get());
    }

}
