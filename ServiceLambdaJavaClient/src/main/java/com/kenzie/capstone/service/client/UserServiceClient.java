package com.kenzie.capstone.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.capstone.service.model.UpdateFriendListRequest;
import com.kenzie.capstone.service.model.User;
import com.kenzie.capstone.service.model.UserCreateRequestLambda;
import com.kenzie.capstone.service.model.UserResponseLambda;

import java.util.List;

public class UserServiceClient {

    private static final String FIND_EXISTING_USER = "user/{userId}";
    private static final String ADD_NEW_USER = "user";
    private static final String REMOVE_FROM_USERS_FRIEND = "user/{userId}/removeFriend";
    private static final String ADD_TO_USERS_FRIENDS = "user/{userId}/addFriend";
    private static final String FIND_FRIENDS = "user/{userId}/friends";

    private ObjectMapper mapper;

    public UserServiceClient() {
        this.mapper = new ObjectMapper();
    }

    public UserResponseLambda findExistingUser(String userId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(FIND_EXISTING_USER.replace("{userId}", userId));
        User user;
        try {
            user = mapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return new UserResponseLambda(user);
    }
    // this will need to be changed to accept userId and username or would it be json for userrequestobject?
    public UserResponseLambda addNewUser(UserCreateRequestLambda userCreateRequestLambda) {
        String request;
        try {
            request = mapper.writeValueAsString(userCreateRequestLambda);
        } catch(JsonProcessingException e) {
            throw new ApiGatewayException("Unable to serialize request: " + e);
        }
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.postEndpoint(ADD_NEW_USER, request);
        User user;
        try {
            user = mapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return new UserResponseLambda(user);
    }

    public List<String> getFriendList(String userId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(FIND_FRIENDS.replace("{userId}", userId));
        List<String> friendList;
        try {
            friendList = mapper.readValue(response, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return friendList;
    }

    public UserResponseLambda addFriend(String userId, String friendId) {
        UpdateFriendListRequest updateFriendListRequest = new UpdateFriendListRequest(userId, friendId);
        String request;
        try {
            request = mapper.writeValueAsString(updateFriendListRequest);
        } catch(JsonProcessingException e) {
            throw new ApiGatewayException("Unable to serialize request: " + e);
        }
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.putEndpoint(ADD_TO_USERS_FRIENDS.replace("{userId}", userId), request);
        User user;
        try {
            user = mapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return new UserResponseLambda(user);
    }

    public UserResponseLambda removeFriend(String userId, String friendId) {
        UpdateFriendListRequest updateFriendListRequest = new UpdateFriendListRequest(userId, friendId);
        String request;
        try {
            request = mapper.writeValueAsString(updateFriendListRequest);
        } catch(JsonProcessingException e) {
            throw new ApiGatewayException("Unable to serialize request: " + e);
        }
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.putEndpoint(
                REMOVE_FROM_USERS_FRIEND.replace("{userId}", userId), request);
        User user;
        try {
            user = mapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return new UserResponseLambda(user);
        // remove a friend from a users list of friends
        // reutrn updated friend list
        // return null;
    }

    // in client -> design response for expected and/or unexpected behavior
}
