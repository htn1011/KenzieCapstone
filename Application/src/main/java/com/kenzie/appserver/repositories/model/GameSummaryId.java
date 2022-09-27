package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class GameSummaryId {
    private String userId;
    private String SummarySortKey;

    public GameSummaryId() {
    }

    public GameSummaryId(String userId, String summarySortKey) {
        this.userId = userId;
        SummarySortKey = summarySortKey;
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
        return SummarySortKey;
    }

    public void setSummarySortKey(String summarySortKey) {
        SummarySortKey = summarySortKey;
    }

    @Override
    public String toString() {
        return "GameSummaryId{" +
                "userId='" + userId + '\'' +
                ", SummarySortKey='" + SummarySortKey + '\'' +
                '}';
    }
}
