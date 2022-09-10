package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName = "GameSummary")
public class GameSummaryRecord {

    private String id;
    private String timeStamp;
    private String userId;
    private String results;

    @DynamoDBHashKey(attributeName = "Id")
    public String getId() {
        return id;
    }

    @DynamoDBRangeKey(attributeName = "TimeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    @DynamoDBAttribute(attributeName = "UserId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBAttribute(attributeName = "Results")
    public String results() {
        return results;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setResults(String results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameSummaryRecord gameSummaryRecord = (GameSummaryRecord) o;
        return Objects.equals(id, gameSummaryRecord.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
