package com.kenzie.capstone.service.client;

import com.amazonaws.AmazonServiceException;

public class ApiGatewayException extends AmazonServiceException {
    public ApiGatewayException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.setStatusCode(statusCode);
    }

    public ApiGatewayException(String errorMessage) {
        super(errorMessage);
    }

    public ApiGatewayException(String errorMessage, Exception cause) {
        super(errorMessage, cause);
    }
}
