package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.List;

// @TODO User Lambda clarification
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    @NotEmpty
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("friendsList")
    private List<String> friendsList;

    public UserResponse(String userId, String userName, List<String> friendsList) {
        this.userId = userId;
        this.userName = userName;
        this.friendsList = friendsList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
    }
}
