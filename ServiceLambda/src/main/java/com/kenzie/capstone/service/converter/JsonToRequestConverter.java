package com.kenzie.capstone.service.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.capstone.service.model.UserCreateRequest;

public class JsonToRequestConverter {
    public UserCreateRequest convert(String body) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            UserCreateRequest referralRequest = gson.fromJson(body, UserCreateRequest.class);
            return referralRequest;
        } catch (Exception e) {
            throw new IllegalArgumentException("Referral could not be deserialized");
        }
    }

}
