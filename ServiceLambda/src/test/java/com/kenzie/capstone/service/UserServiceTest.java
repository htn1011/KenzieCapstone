package com.kenzie.capstone.service;

import com.kenzie.capstone.service.dao.UserDao;
import com.kenzie.capstone.service.model.*;
import com.kenzie.capstone.service.model.NoExistingUserException;
import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class  UserServiceTest {

    /** ------------------------------------------------------------------------
     *  expenseService.getExpenseById
     *  ------------------------------------------------------------------------ **/

    private UserDao userDao;
    private UserService userService;

    @BeforeAll
    void setup() {
        this.userDao = mock(UserDao.class);
        this.userService = new UserService(userDao);
    }

    // @Test
    // void setDataTest() {
    //     ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    //     ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
    //
    //     // GIVEN
    //     String data = "somedata";
    //
    //     // WHEN
    //     User response = this.userService.addUser(mock(UserCreateRequestLambda.class));
    //
    //     // THEN
    //     verify(userDao, times(1)).addNewUser(idCaptor.capture(), dataCaptor.capture());
    //
    //     assertNotNull(idCaptor.getValue(), "An ID is generated");
    //     assertEquals(data, dataCaptor.getValue(), "The data is saved");
    //
    //     assertNotNull(response, "A response is returned");
    //     assertEquals(idCaptor.getValue(), response.getUserId(), "The response id should match");
    //     assertEquals(data, response.getUsername(), "The response data should match");
    // }

    // @Test
    // void getDataTest() {
    //     ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    //
    //     // GIVEN
    //     String id = "fakeid";
    //     String data = "somedata";
    //     UserRecord record = new UserRecord();
    //     record.setUserId(id);
    //     record.setUsername(data);
    //
    //
    //     when(userDao.findUserName(id)).thenReturn(Arrays.asList(record));
    //
    //     // WHEN
    //     User response = this.userService.findUser(id);
    //
    //     // THEN
    //     verify(userDao, times(1)).findUserName(idCaptor.capture());
    //
    //     assertEquals(id, idCaptor.getValue(), "The correct id is used");
    //
    //     assertNotNull(response, "A response is returned");
    //     assertEquals(id, response.getUserId(), "The response id should match");
    //     assertEquals(data, response.getUsername(), "The response data should match");
    // }

    // Write additional tests here
    @Test
    void findUserTest() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        when(userDao.findByUserId(userId)).thenReturn(userRecord);

        //WHEN
        Optional<User> user = userService.findUser(userId);

        //THEN
        Assertions.assertNotNull(user, "User exists");
        Assertions.assertEquals(userId, user.get().getUserId(), "UserId matches");
        Assertions.assertEquals(userName, user.get().getUsername(), "Username matches");
        Assertions.assertEquals(friendsList, user.get().getFriendsList(), "Friends list matches");
    }

    @Test
    void findUserTest_InvalidUser_ThrowsException() {
        //GIVEN
        String userId = "userId";
        doThrow(NoExistingUserException.class).when(userDao).findByUserId(userId);

        //THEN
        Assertions.assertThrows(NoExistingUserException.class, () -> userService.findUser(userId), "User does not exist");
    }

    @Test
    void addUserTest() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserCreateRequestLambda userCreateRequestLambda = new UserCreateRequestLambda(userId, userName);
        UserRecord userRecordNull = new UserRecord();
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        when(userDao.addNewUser(userCreateRequestLambda.getUserId(), userCreateRequestLambda.getUsername())).thenReturn(userRecord);

        //WHEN
        User user = userService.addUser(userCreateRequestLambda);

        //THEN
        Assertions.assertNotNull(user, "User not null");
        Assertions.assertEquals(userId, user.getUserId(), "UserId matches");
        Assertions.assertEquals(userName, user.getUsername(), "UserName matches");
        Assertions.assertEquals(friendsList, user.getFriendsList(), "Friendslist matches");
        Assertions.assertNotNull(user.getUserId(), "UserId exists");
        Assertions.assertNotNull(user.getUsername(), "UserName exists");
    }

    @Test
    void addUserTest_UserExist_ThrowsException() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        UserCreateRequestLambda userCreateRequestLambda = new UserCreateRequestLambda(userId, userName);
        when(userDao.findByUserId(userId)).thenReturn(userRecord);

        //THEN
        Assertions.assertThrows(UserAlreadyExistsException.class, ()-> userService.addUser(userCreateRequestLambda), "User already exists");
    }

    @Test
    void getFriendsTest() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        when(userDao.findByUserId(userId)).thenReturn(userRecord);

        //WHEN
        List<String> returnedFriendsList = userService.getFriends(userId);

        //THEN
        verify(userDao, atLeastOnce()).findByUserId(any());
        Assertions.assertEquals(friendsList, returnedFriendsList, "Friends list matches");
    }

    @Test
    void getFriendsTest_InvalidUser_ThrowsException() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        doThrow(NoExistingUserException.class).when(userDao).findByUserId(userId);

        //THEN
        Assertions.assertThrows(NoExistingUserException.class, () -> userService.getFriends(userId), "User does not exist");
    }

    @Test
    void addFriendTest() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        String friendId = "friendId";
        String friendUserName = "friendUserName";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        List<String> friendsList1 = new ArrayList<>();
        friendsList1.add(friend);
        friendsList1.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        UserRecord friendRecord = new UserRecord();
        friendRecord.setUserId(friendId);
        friendRecord.setUsername(friendUserName);
        friendRecord.setFriendsList(friendsList1);
        when(userDao.findByUserId(userId)).thenReturn(userRecord);
        when(userDao.findByUserId(friendId)).thenReturn(friendRecord);

        //WHEN
        User updatedUser = userService.addfriend(userId, friendId);

        //THEN
        verify(userDao, times(2)).findByUserId(any());
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(userId, updatedUser.getUserId(), "UserId matches");
        Assertions.assertEquals(userName, updatedUser.getUsername(), "UserName matches");
        Assertions.assertEquals(3, updatedUser.getFriendsList().size(), "FriendList matches");
        Assertions.assertTrue(updatedUser.getFriendsList().contains(friendId), "Added friend is in list");
    }

    @Test
    void addFriendTest_InvalidUser_ThrowsException() {
        //GIVEN
        String userId = "";
        String friendId = "";
        doThrow(NoExistingUserException.class).when(userDao).findByUserId(userId);

        //THEN
        Assertions.assertThrows(NoExistingUserException.class, ()-> userService.addfriend(userId, friendId), "User does not exist");
    }

    @Test
    void addFriendTest_InvalidFriend_ThrowsException() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        String newFriend = "";
        when(userDao.findByUserId(userId)).thenReturn(userRecord);
        doThrow(NoExistingUserException.class).when(userDao).findByUserId(newFriend);

        //THEN
        Assertions.assertThrows(NoExistingUserException.class, () -> userService.addfriend(userId, newFriend), "Friend does not exist");
    }

    @Test
    void removeFriendTest() {
        //GIVEN
        String userId = "userId";
        String userName = "userName";
        String friend = "friend";
        String friend1 = "friend1";
        List<String> friendsList = new ArrayList<>();
        friendsList.add(friend);
        friendsList.add(friend1);
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userId);
        userRecord.setUsername(userName);
        userRecord.setFriendsList(friendsList);
        when(userDao.findByUserId(userId)).thenReturn(userRecord);

        //WHEN
        User user = userService.removeFriend(userId, friend);

        //THEN
        verify(userDao, atLeastOnce()).findByUserId(userId);
        Assertions.assertFalse(user.getFriendsList().contains(friend), "Friend is removed from list");
        Assertions.assertEquals(1, user.getFriendsList().size(), "Friend list has only one friend in list");
    }

    @Test
    void removeFriendTest_InvalidUser_ThrowsException() {
        //GIVEN
        String userId = "userId";
        String friendId = "friendId";
        doThrow(NoExistingUserException.class).when(userDao).findByUserId(userId);

        //THEN
        assertThrows(NoExistingUserException.class, ()-> userService.removeFriend(userId, friendId), "UserId not found");
    }
}