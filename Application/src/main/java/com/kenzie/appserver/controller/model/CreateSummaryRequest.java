package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;


public class CreateSummaryRequest {
    @NotEmpty
    @JsonProperty("gameId")
    private String gameId;

    @NotEmpty
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("results")
    private String results;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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
