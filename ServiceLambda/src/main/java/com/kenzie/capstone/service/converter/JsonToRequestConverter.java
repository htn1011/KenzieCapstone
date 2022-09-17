package com.kenzie.capstone.service.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.capstone.service.model.UpdateFriendListRequest;
import com.kenzie.capstone.service.model.UserCreateRequestLambda;

public class JsonToRequestConverter {
    public UserCreateRequestLambda convertToUserCreateRequest(String body) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(body, UserCreateRequestLambda.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Referral could not be deserialized");
        }
    }

    public UpdateFriendListRequest convertToUpdateFriendListRequest(String body) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(body, UpdateFriendListRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Referral could not be deserialized");
        }
    }

}
