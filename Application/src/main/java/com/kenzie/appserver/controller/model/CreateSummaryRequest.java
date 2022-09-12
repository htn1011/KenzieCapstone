package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;


public class CreateSummaryRequest {
    @NotEmpty
    @JsonProperty("gameName")
    private String gameName;
    // since we decided to format the key for the game ID in some concatination, when the incoming request comes in
    // it should contain the raw data the key will be formatted from in the service class

    @NotEmpty
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("results")
    private String results;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
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
