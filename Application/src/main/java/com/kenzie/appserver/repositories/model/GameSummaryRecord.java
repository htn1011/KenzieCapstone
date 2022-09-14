package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@DynamoDBTable(tableName = "GameSummaries")
public class GameSummaryRecord {

    //https://medium.com/@leohoc/dynamodb-and-spring-data-a81c546a1305
    // using @Id and gameSummaryId obj to use composite key
    @Id
    private GameSummaryId gameSummaryId;
    // partition key in key schema
    private String userId;
    private String game;
    // GSI hash key
    private String date;
    private String results;
    private String sessionNumber;
    // game::date eg. wordle::09-13-2022
    private String summarySortKey;
    // game::userId eg. wordle::Henry
    private String indexSortKey;

    // compsite key: userId and summarySortKey(Game::date)
    // index: date and indexSortKey(game::userId)

    // extra things in record -> ID obj and the two formatted sortkeys - only needed in DB.
    // is needed in cache - made GameSummary Obj in service that has these too
    // layer between controller models and record models

    public GameSummaryRecord() {
    }

    public GameSummaryRecord(String userId, String game, String date, String results, String sessionNumber) {
        this.userId = userId;
        this.game = game;
        this.date = date;
        this.results = results;
        this.sessionNumber = sessionNumber;
        // format the sort keys when creating obj
        this.summarySortKey = String.format("%s::%s", game, date);
        this.indexSortKey = String.format("%s::%s", game, userId);
        // create the ID obj
        this.gameSummaryId = new GameSummaryId(userId, summarySortKey);
    }



    public GameSummaryId getGameSummaryId() {
        return gameSummaryId;
    }
    public void setGameSummaryId(GameSummaryId gameSummaryId) {
        this.gameSummaryId = gameSummaryId;
    }

    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return gameSummaryId != null ? gameSummaryId.getUserId() : null;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = "date")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "WordleDateIndex")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBRangeKey(attributeName = "summarySortKey")
    public String getSummarySortKey() {
        return gameSummaryId != null ? gameSummaryId.getSummarySortKey() : null;
    }

    public void setSummarySortKey(String summarySortKey) {
        this.summarySortKey = summarySortKey;
    }

    @DynamoDBAttribute(attributeName = "indexSortKey")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "WordleDateIndex")
    public String getIndexSortKey() {
        return indexSortKey;
    }

    public void setIndexSortKey(String indexSortKey) {
        this.indexSortKey = indexSortKey;
    }

    @DynamoDBAttribute(attributeName = "game")
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    @DynamoDBAttribute(attributeName = "results")
    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    @DynamoDBAttribute(attributeName = "sessionNumber")
    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    // equality based on userId, game, date, results and session number
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameSummaryRecord that = (GameSummaryRecord) o;
        return Objects.equals(userId, that.userId) && Objects.equals(
                game,
                that.game) && Objects.equals(date, that.date) && Objects.equals(
                results,
                that.results) && Objects.equals(sessionNumber, that.sessionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, game, date, results, sessionNumber);
    }
}
