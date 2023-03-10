package com.kenzie.capstone.service.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.capstone.service.UserService;
import com.kenzie.capstone.service.converter.JsonToRequestConverter;
import com.kenzie.capstone.service.dependency.DaggerServiceComponent;
import com.kenzie.capstone.service.dependency.ServiceComponent;
import com.kenzie.capstone.service.model.UpdateFriendListRequest;
import com.kenzie.capstone.service.model.User;
import com.kenzie.capstone.service.model.UserAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AddNewFriend implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    // lambda to add new friend to user's friend list
    static final Logger log = LogManager.getLogger();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        JsonToRequestConverter converter = new JsonToRequestConverter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        log.info(gson.toJson(input));

        ServiceComponent serviceComponent = DaggerServiceComponent.create();
        UserService userService = serviceComponent.provideLambdaService();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // input body will be the UpdateFriendListRequest Model - need to be converted from JSON
        String data = input.getBody();

        if (data == null || data.length() == 0) {
            return response
                    .withStatusCode(400)
                    .withBody("data is invalid");
        }

        try {
            UpdateFriendListRequest updateFriendListRequest = converter.convertToUpdateFriendListRequest(data);
            User updatedUser = userService.addFriend(
                    updateFriendListRequest.getUserId(),
                    updateFriendListRequest.getFriendId());
            String output = gson.toJson(updatedUser);

            return response
                    .withStatusCode(200)
                    .withBody(output);

        } catch (UserAlreadyExistsException e) {
            return response
                    .withStatusCode(409)
                    .withBody(e.getMessage());
        } catch (Exception e) {
            return response
                    .withStatusCode(400)
                    .withBody(gson.toJson(e.getMessage()));
        }
    }
}
