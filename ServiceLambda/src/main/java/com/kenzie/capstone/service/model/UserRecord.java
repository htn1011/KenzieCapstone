package com.kenzie.capstone.service.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@DynamoDBTable(tableName = "Users")
public class UserRecord {

    private String userId;
    private String username;
    private List<String> friendsList;

    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    @DynamoDBAttribute(attributeName = "friendsList")
    public List<String> getFriendsList() {
        return friendsList;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFriendsList(List<String> friendsList) {
        this.friendsList = Optional.ofNullable(friendsList).orElseGet(ArrayList::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRecord that = (UserRecord) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }
}
