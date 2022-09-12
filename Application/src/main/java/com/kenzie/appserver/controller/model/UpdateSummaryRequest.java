package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class UpdateSummaryRequest {
    @NotEmpty
    @JsonProperty("summaryId")
    private String summaryId;
    // the user doesnt have to remember this - can be saved on button click when selecting which summary to update

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("results")
    private String results;

    public UpdateSummaryRequest(String summaryId, String userId, String results) {
        this.summaryId = summaryId;
        this.userId = userId;
        this.results = results;
    }

    public String getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(String summaryId) {
        this.summaryId = summaryId;
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
