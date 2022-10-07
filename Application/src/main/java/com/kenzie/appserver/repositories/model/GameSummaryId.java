package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class GameSummaryId {
    private String userId;
    private String summarySortKey;

    public GameSummaryId() {
    }

    public GameSummaryId(String userId, String summarySortKey) {
        this.userId = userId;
        this.summarySortKey = summarySortKey;
    }

    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = "summarySortKey")
    public String getSummarySortKey() {
        return summarySortKey;
    }

    public void setSummarySortKey(String summarySortKey) {
        this.summarySortKey = summarySortKey;
    }

    @Override
    public String toString() {
        return "GameSummaryId{" +
                "userId='" + userId + '\'' +
                ", SummarySortKey='" + summarySortKey + '\'' +
                '}';
    }
}
