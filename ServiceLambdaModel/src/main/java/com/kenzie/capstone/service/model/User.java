package com.kenzie.capstone.service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class User {
    private String userId;
    private String username;
    private List<String> friendsList;

    public User(String userId, String username, List<String> friendsList) {
        this.userId = userId;
        this.username = username;
        this.friendsList = Optional.ofNullable(friendsList).orElseGet(ArrayList::new);
    }

    public User() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(
                username,
                user.username) && Objects.equals(friendsList, user.friendsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, friendsList);
    }
}
