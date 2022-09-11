package com.kenzie.appserver.service.conversion;

import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.SummaryResponse;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;

import java.time.LocalDateTime;

public class GameSummaryConversion {

    public static GameSummaryRecord createRequestToRecord(CreateSummaryRequest createSummaryRequest) {
        GameSummaryRecord summaryRecord = new GameSummaryRecord();

        summaryRecord.setUserId(createSummaryRequest.getUserId());
        summaryRecord.setId(createSummaryRequest.getGameId());
        summaryRecord.setResults(createSummaryRequest.getResults());
        summaryRecord.setTimeStamp(String.valueOf(LocalDateTime.now()));

        return summaryRecord;
    }

    public static SummaryResponse recordToResponse(GameSummaryRecord gameSummaryRecord) {
        SummaryResponse response = new SummaryResponse();

        response.setGameId(gameSummaryRecord.getId());
        response.setResults(gameSummaryRecord.results());
        response.setUserId(gameSummaryRecord.getUserId());
        response.setTimestamp(gameSummaryRecord.getTimeStamp());

        return response;
    }
}
