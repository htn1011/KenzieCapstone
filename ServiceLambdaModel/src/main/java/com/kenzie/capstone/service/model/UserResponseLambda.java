package com.kenzie.capstone.service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserResponseLambda {
    private String userId;

    private String userName;

    private List<String> friendsList;

    public UserResponseLambda() {
    }

    public UserResponseLambda(String userId, String userName, List<String> friendsList) {
        this.userId = userId;
        this.userName = userName;
        this.friendsList = Optional.ofNullable(friendsList).orElse(new ArrayList<>());
    }

    public UserResponseLambda(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUsername();
        this.friendsList = Optional.ofNullable(user.getFriendsList()).orElseGet(ArrayList::new);
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
