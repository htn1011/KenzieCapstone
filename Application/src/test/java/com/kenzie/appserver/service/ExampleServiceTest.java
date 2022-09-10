package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.GameRepository;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;
import com.kenzie.appserver.service.model.Example;
import com.kenzie.capstone.service.client.UserServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExampleServiceTest {
    private GameRepository gameRepository;
    private ExampleService exampleService;
    private UserServiceClient userServiceClient;

    @BeforeEach
    void setup() {
        gameRepository = mock(GameRepository.class);
        userServiceClient = mock(UserServiceClient.class);
        exampleService = new ExampleService(gameRepository, userServiceClient);
    }
    /** ------------------------------------------------------------------------
     *  exampleService.findById
     *  ------------------------------------------------------------------------ **/

    @Test
    void findById() {
        // GIVEN
        String id = randomUUID().toString();

        GameSummaryRecord record = new GameSummaryRecord();
        record.setId(id);
        record.setName("concertname");

        // WHEN
        when(gameRepository.findById(id)).thenReturn(Optional.of(record));
        Example example = exampleService.findById(id);

        // THEN
        Assertions.assertNotNull(example, "The object is returned");
        Assertions.assertEquals(record.getId(), example.getId(), "The id matches");
        Assertions.assertEquals(record.getDate(), example.getName(), "The name matches");
    }

    @Test
    void findByConcertId_invalid() {
        // GIVEN
        String id = randomUUID().toString();

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        Example example = exampleService.findById(id);

        // THEN
        Assertions.assertNull(example, "The example is null when not found");
    }

}
