package com.kenzie.capstone.service.model;

public class UpdateFriendListRequest {
    private String userId;
    private String friendId;

    public UpdateFriendListRequest(String userId, String friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return friendId;
    }

    public void setUsername(String friendId) {
        this.friendId = friendId;
    }
}
