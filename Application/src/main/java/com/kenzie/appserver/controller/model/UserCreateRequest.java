package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class UserCreateRequest {
    @NotEmpty
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("username")
    private String username;
    // todo change naming so consistent throughout

    public UserCreateRequest(String userId, String userName) {
        this.userId = userId;
        this.username = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getusername() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }
}
