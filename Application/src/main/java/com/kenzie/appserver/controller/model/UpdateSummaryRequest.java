package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class UpdateSummaryRequest {
    @NotEmpty
    @JsonProperty("existingSummaryDate")
    private String existingSummaryDate;
    // the user doesnt have to remember this - can be saved on button click when selecting which summary to update

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("game")
    private String game;

    @JsonProperty("updatedResults")
    private String updatedResults;

    public UpdateSummaryRequest(String existingSummaryDate, String userId, String game, String updatedResults) {
        this.existingSummaryDate = existingSummaryDate;
        this.userId = userId;
        this.game = game;
        this.updatedResults = updatedResults;
    }

    public String getExistingSummaryDate() {
        return existingSummaryDate;
    }

    public void setExistingSummaryDate(String existingSummaryDate) {
        this.existingSummaryDate = existingSummaryDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getUpdatedResults() {
        return updatedResults;
    }

    public void setUpdatedResults(String updatedResults) {
        this.updatedResults = updatedResults;
    }
}
