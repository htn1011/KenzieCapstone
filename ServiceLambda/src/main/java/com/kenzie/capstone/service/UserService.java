package com.kenzie.capstone.service;

import com.kenzie.capstone.service.model.User;
import com.kenzie.capstone.service.dao.UserDao;
import com.kenzie.capstone.service.model.UserCreateRequest;
import com.kenzie.capstone.service.model.UserRecord;

import javax.inject.Inject;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class UserService {

    private UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findUser(String id) {
        // List<UserRecord> records = userDao.findUserName(id);
        // if (records.size() > 0) {
        //     return new User(records.get(0).getUserId(), records.get(0).getUsername());
        // }
        // return null;
        UserRecord userRecord = Optional.ofNullable(userDao.findByUserId(id))
                // check type of exception - custom?
                .orElseThrow(IllegalAccessError::new);

        return new User(userRecord.getUserId(), userRecord.getUsername());

    }

    public User addUser(UserCreateRequest createRequest) {
        // String id = UUID.randomUUID().toString();
        UserRecord record = userDao.addNewUser(createRequest.getUserId(), createRequest.getUsername());
        return new User(record.getUserId(), record.getUsername());
    }
}
