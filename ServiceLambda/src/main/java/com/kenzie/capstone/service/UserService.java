package com.kenzie.capstone.service;

import com.kenzie.capstone.service.model.NoExistingUserException;
import com.kenzie.capstone.service.model.User;
import com.kenzie.capstone.service.dao.UserDao;
import com.kenzie.capstone.service.model.UserAlreadyExistsException;
import com.kenzie.capstone.service.model.UserCreateRequestLambda;
import com.kenzie.capstone.service.model.UserRecord;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

public class UserService {

    private UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> findUser(String id) {

        return Optional.ofNullable(userDao.findByUserId(id))
                .map(userRecord -> new User(
                        userRecord.getUserId(),
                        userRecord.getUsername(),
                        userRecord.getFriendsList()));

    }

    public User addUser(UserCreateRequestLambda createRequest) {
        if (findUser(createRequest.getUserId()).isPresent()) {
            throw new UserAlreadyExistsException(createRequest.getUserId());
        }
        UserRecord record = userDao.addNewUser(createRequest.getUserId(), createRequest.getUsername());
        return new User(record.getUserId(), record.getUsername(), record.getFriendsList());
    }

    public List<String> getFriends(String userId) {
        User existingUser = findUser(userId)
                .orElseThrow(() -> new NoExistingUserException(userId));
        return existingUser.getFriendsList();
    }

    public User addFriend(String userId, String friendId) {
        // make sure user exists
        User existingUser = findUser(userId)
                .orElseThrow(() -> new NoExistingUserException(userId));
        // make sure friend exists
        User friend = findUser(friendId)
                .orElseThrow(() -> new NoExistingUserException(friendId));
        List<String> friendList = existingUser.getFriendsList();
        friendList.add(friendId);
        existingUser.setFriendsList(friendList);
        UserRecord updatedUser = new UserRecord();
        updatedUser.setUserId(existingUser.getUserId());
        updatedUser.setUsername(existingUser.getUsername());
        updatedUser.setFriendsList(existingUser.getFriendsList());

        userDao.updateUser(updatedUser);
        return existingUser;
    }

    public User removeFriend(String userId, String friendId) {
        // make sure user exists
        User existingUser = findUser(userId)
                .orElseThrow(() -> new NoExistingUserException(userId));
        List<String> friendList = existingUser.getFriendsList();
        friendList.removeIf(id -> id.equals(friendId));
        existingUser.setFriendsList(friendList);
        UserRecord updatedUser = new UserRecord();
        updatedUser.setUserId(existingUser.getUserId());
        updatedUser.setUsername(existingUser.getUsername());
        updatedUser.setFriendsList(existingUser.getFriendsList());

        userDao.updateUser(updatedUser);
        return existingUser;
    }

}
