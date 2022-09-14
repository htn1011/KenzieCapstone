package com.kenzie.capstone.service.model;

public class NoExistingUserException extends RuntimeException{
    public NoExistingUserException(String userId) {
        super("No existing user with the userId: " + userId);
    }
}
