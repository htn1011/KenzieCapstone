package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryResponse {
    @JsonProperty("summaryId")
    private String summaryId;   // @TODO to be formatted and created in the GameSummaryService

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("results")
    private String results;


    public SummaryResponse(String summaryId, String userId, String timestamp, String results) {
        this.summaryId = summaryId;
        this.userId = userId;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
