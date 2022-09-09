package com.kenzie.capstone.service.dao;

import com.kenzie.capstone.service.model.UserRecord;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.google.common.collect.ImmutableMap;

import java.util.List;

public class UserDao {
    private DynamoDBMapper mapper;

    /**
     * Allows access to and manipulation of Match objects from the data store.
     * @param mapper Access to DynamoDB
     */
    public UserDao(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    // add new user
    // public UserRecord addNewUser(UserRecord userRecord) {
    //     try {
    //         mapper.save(userRecord, new DynamoDBSaveExpression()
    //                 .withExpected(ImmutableMap.of(
    //                         "userId",
    //                         new ExpectedAttributeValue().withExists(false)
    //                 )));
    //     } catch (ConditionalCheckFailedException e) {
    //         throw new IllegalArgumentException("userId has already been used");
    //     }
    //
    //     return userRecord;
    // }

    // find an existing user
    public UserRecord findByUserId(String userId) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        return mapper.load(userRecord);
    }

    public List<UserRecord> findUserName(String userId) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);

        DynamoDBQueryExpression<UserRecord> queryExpression = new DynamoDBQueryExpression<UserRecord>()
                .withHashKeyValues(userRecord)
                .withConsistentRead(false);

        return mapper.query(UserRecord.class, queryExpression);
    }



    public UserRecord addNewUser(String userId, String username) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(username);

        try {
            mapper.save(userRecord, new DynamoDBSaveExpression()
                    .withExpected(ImmutableMap.of(
                            "userId",
                            new ExpectedAttributeValue().withExists(false)
                    )));
        } catch (ConditionalCheckFailedException e) {
            throw new IllegalArgumentException("user ID already exists");
        }

        return userRecord;
    }
}
