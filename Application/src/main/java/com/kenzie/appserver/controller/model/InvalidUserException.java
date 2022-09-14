package com.kenzie.appserver.controller.model;

import com.kenzie.capstone.service.client.ApiGatewayException;
import org.springframework.http.HttpStatus;

public class InvalidUserException extends ApiGatewayException {
    public InvalidUserException(String invalidUserId) {
        super("There is no user with ID: " + invalidUserId);
    }

    public InvalidUserException(String invalidUserId, Exception cause) {
        super("There is no user with ID: " + invalidUserId, cause);
        this.setStatusCode(HttpStatus.NOT_FOUND.value());
    }
}
