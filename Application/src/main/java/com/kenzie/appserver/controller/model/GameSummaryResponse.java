package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameSummaryResponse {

    @JsonProperty("game")
    private String game;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("sessionNumber")
    private String sessionNumber;

    @JsonProperty("results")
    private String results;

    public GameSummaryResponse(String game, String userId, String date, String sessionNumber, String results) {
        this.game = game;
        this.userId = userId;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
