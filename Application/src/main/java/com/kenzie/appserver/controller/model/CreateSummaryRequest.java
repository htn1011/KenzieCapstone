package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;


public class CreateSummaryRequest {
    @NotEmpty
    @JsonProperty("gameId")
    private String gameId;
    // since we decided to format the key for the game ID in some concatination, when the incoming request comes in
    // it should contain the raw data the key will be formatted from in the service class
    // @TODO I think can pass in seperately in the request and it will format as concatenated key in response

    @NotEmpty
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("results")
    private String results;

    public CreateSummaryRequest(String gameId, String sessionId, String userId, String results) {
        this.gameId = gameId;
        this.sessionId = sessionId;
        this.userId = userId;
        this.results = results;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
