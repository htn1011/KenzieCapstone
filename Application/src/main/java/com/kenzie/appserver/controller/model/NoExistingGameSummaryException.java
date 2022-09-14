package com.kenzie.appserver.controller.model;

import com.kenzie.capstone.service.client.ApiGatewayException;
import org.springframework.http.HttpStatus;

public class NoExistingGameSummaryException extends ApiGatewayException {
    public NoExistingGameSummaryException(String game, String summaryDate, String userId) {
        super("No existing " + game + " game summary for user: " + userId + " for the date: " + summaryDate);
        this.setStatusCode(HttpStatus.NOT_FOUND.value());
    }
}
