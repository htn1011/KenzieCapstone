package com.kenzie.capstone.service;

import com.kenzie.capstone.service.model.User;
import com.kenzie.capstone.service.dao.UserDao;
import com.kenzie.capstone.service.model.UserCreateRequestLambda;
import com.kenzie.capstone.service.model.UserRecord;

import javax.inject.Inject;

import java.util.Optional;

public class UserService {

    private UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> findUser(String id) {

        return Optional.of(userDao.findByUserId(id))
                .map(userRecord -> new User(
                        userRecord.getUserId(),
                        userRecord.getUsername(),
                        userRecord.getFriendsList()));

    }

    public User addUser(UserCreateRequestLambda createRequest) {
        UserRecord record = userDao.addNewUser(createRequest.getUserId(), createRequest.getUsername());
        return new User(record.getUserId(), record.getUsername(), record.getFriendsList());
    }
}
