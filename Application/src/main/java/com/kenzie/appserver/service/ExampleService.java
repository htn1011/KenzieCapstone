package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.appserver.repositories.GameRepository;
import com.kenzie.appserver.service.model.Example;

import com.kenzie.capstone.service.client.UserServiceClient;
import com.kenzie.capstone.service.model.User;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {
    private GameRepository gameRepository;
    private UserServiceClient userServiceClient;

    public ExampleService(GameRepository gameRepository, UserServiceClient userServiceClient) {
        this.gameRepository = gameRepository;
        this.userServiceClient = userServiceClient;
    }

    public Example findById(String id) {

        // Example getting data from the lambda
        User dataFromLambda = userServiceClient.getExampleData(id);

        // Example getting data from the local repository
        Example dataFromDynamo = gameRepository
                .findById(id)
                .map(example -> new Example(example.getId(), example.getDate()))
                .orElse(null);

        return dataFromDynamo;
    }

    public Example addNewExample(String name) {
        // Example sending data to the lambda
        User dataFromLambda = userServiceClient.setExampleData(name);

        // Example sending data to the local repository
        GameSummaryRecord exampleRecord = new GameSummaryRecord();
        exampleRecord.setId(dataFromLambda.getUserId());
        exampleRecord.setName(dataFromLambda.getUsername());
        gameRepository.save(exampleRecord);

        Example example = new Example(dataFromLambda.getUserId(), name);
        return example;
    }
}
