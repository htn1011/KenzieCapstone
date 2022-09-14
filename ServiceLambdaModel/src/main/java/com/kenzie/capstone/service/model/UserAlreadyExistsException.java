package com.kenzie.capstone.service.model;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String existingUserId) {
        super("User already exists with the user ID: " + existingUserId);
    }
}
