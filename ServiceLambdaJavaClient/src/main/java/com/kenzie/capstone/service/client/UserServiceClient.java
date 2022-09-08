package com.kenzie.capstone.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.capstone.service.model.User;


public class UserServiceClient {

    private static final String VERIFY_USER = "user/{userId}";
    private static final String ADD_NEW_USER = "user";

    private ObjectMapper mapper;

    public UserServiceClient() {
        this.mapper = new ObjectMapper();
    }

    public User getExampleData(String userId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(VERIFY_USER.replace("{userId}", userId));
        User user;
        try {
            user = mapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return user;
    }
    // this will need to be changed to accept userId and username or would it be json for userrequestobject?
    public User setExampleData(String data) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.postEndpoint(ADD_NEW_USER, data);
        User user;
        try {
            user = mapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return user;
    }
}
