package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class CreateSummaryRequest {
    @NotEmpty
    @JsonProperty("game")
    private String game;

    @NotEmpty
    @JsonProperty("userId")
    private String userId;


    @JsonProperty("sessionNumber")
    private String sessionNumber;

    @JsonProperty("results")
    private String results;

    public CreateSummaryRequest(String game, String userId, String sessionNumber, String results) {
        this.game = game;
        this.userId = userId;
        this.sessionNumber = sessionNumber;
        this.results = results;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }


}
