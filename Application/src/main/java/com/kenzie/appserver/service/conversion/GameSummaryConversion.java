package com.kenzie.appserver.service.conversion;

import com.kenzie.appserver.controller.model.CreateSummaryRequest;
import com.kenzie.appserver.controller.model.GameSummaryResponse;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;

public class GameSummaryConversion {
    public static GameSummaryRecord createRequestToRecord(CreateSummaryRequest createSummaryRequest) {
        return new GameSummaryRecord(
                createSummaryRequest.getUserId(),
                createSummaryRequest.getGame(),
                createSummaryRequest.getDate(),
                createSummaryRequest.getResults(),
                createSummaryRequest.getSessionNumber());
    }

    public static GameSummaryResponse recordToResponse(GameSummaryRecord gameSummaryRecord) {
        return new GameSummaryResponse(
                gameSummaryRecord.getGame(),
                gameSummaryRecord.getUserId(),
                gameSummaryRecord.getDate(),
                gameSummaryRecord.getSessionNumber(),
                gameSummaryRecord.getResults());
    }
}
