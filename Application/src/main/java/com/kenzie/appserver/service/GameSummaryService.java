package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.SummaryResponse;
import com.kenzie.appserver.repositories.GameRepository;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.appserver.service.conversion.GameSummaryConversion;
import com.kenzie.capstone.service.client.UserServiceClient;
import com.kenzie.capstone.service.model.User;
import com.kenzie.capstone.service.model.UserCreateRequest;
import com.kenzie.capstone.service.model.UserResponse;

import java.util.List;
import java.util.Optional;

public class GameSummaryService {
    private GameRepository gameRepository;
    private UserServiceClient userServiceClient;

    public GameSummaryService(GameRepository gameRepository, UserServiceClient userServiceClient) {
        this.gameRepository = gameRepository;
        this.userServiceClient = userServiceClient;
    }

    public UserResponse addNewUser(UserCreateRequest userCreateRequest) {
        // request comes from the controller to add a new user
        // calls the user client
        return null;
    }

    public UserResponse verifyUser(String userId) {
        // calls the user client to verify if user exists
        return null;
    }

    public SummaryResponse addSummary(CreateSummaryRequest summaryRequest) {
        // steps:
        // use the userService client to verify the user
        // if the user exists ? continue : give the user an option to create a new user or post without a user ID
        // create a new summary record and save it to the repo
        // format the key according to the helper method

        // User dataFromLambda = userServiceClient.addNewUser(summaryRequest.getUserId());

        //if user is null, do we want to throw an exception here or handle it in the userServiceClient?

        GameSummaryRecord record = GameSummaryConversion.createRequestToRecord(summaryRequest);
        gameRepository.save(record);

        return GameSummaryConversion.recordToResponse(record);
    }

    public SummaryResponse getSummary(String gameId, String userId) {
        User dataFromLambda = userServiceClient.verifyUser(userId);

        // if user is null, how do we handle this? Is the exception thrown in the method for userServiceClient checking for null?

        Optional<GameSummaryRecord> record = gameRepository.findById(gameId);

        if (record.isEmpty()) {
            //how do we handle this?
        }

        return GameSummaryConversion.recordToResponse(record.get());
    }

    public List<SummaryResponse> getAllSummaries() {
        return null;
    }

    public List<SummaryResponse> getUserSummaries(String userId) {
        return null;
    }

    private String formatKey(String gameName, String sessionId) {

        return String.format("%s::%s", gameName, sessionId);
    }

}
