import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import SummaryClient from "../api/summaryClient";

// display states
const DISPLAY_STATE = "userPageDisplay";
const EDIT_STATE = "editFriendList";
const LOGIN_STATUS = "loginStatus";
// display state variations
const EDIT_YES = "yes";
const EDIT_NO = "no";
const LOGIN = "login";
const SIGN_UP = "signUp";
const USER_INFO = "userInfo";
const LOGIN_SUCCESS = "success";
// const variables
const USER = "user";

/**
 * Logic needed for the user to log in or sign up.
 */
class UserLoginPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['onUserLogin', 'onRequestSignUp', 'onUserSignUp', 'render', 'onRemoveFriend',
        'onRequestEditFriends', 'onAddFriend', 'userNotFoundErrorHandler'], this);
        this.dataStore = new DataStore();
    }

    /**
     * Once the page has loaded, set up the event handlers
     */
    async mount() {
        // listeners
        document.getElementById('login-form').addEventListener('submit', this.onUserLogin);
        document.getElementById('sign-up-request').addEventListener('click', this.onRequestSignUp);
        document.getElementById('sign-up-form').addEventListener('submit', this.onUserSignUp);
        document.getElementById('edit-friends').addEventListener('click', this.onRequestEditFriends);
        document.getElementById('add-friend').addEventListener('click', this.onAddFriend);
        document.getElementById('remove-friend').addEventListener('click', this.onRemoveFriend);
        this.client = new SummaryClient();
        // if the user has already logged in - show user info otherwise login page
        this.user = this.dataStore.get(USER);
        this.dataStore.set(EDIT_STATE, EDIT_NO );
        if (this.user == undefined) {
            this.dataStore.set(DISPLAY_STATE, LOGIN);
        } else {
            this.dataStore.set(DISPLAY_STATE, USER_INFO);
        }
        // re-render whenever change made to datastore
        this.dataStore.addChangeListener(this.render);
        this.render();
    }
    // Render Methods -------------------------------------------------------------------------------------------------

    async render() {
        // userPageDisplay signifies the state of the page -> login/signup/userInfo
        let userPageDisplay = this.dataStore.get(DISPLAY_STATE);
        // editFriendList signifies the state of whether to edit the friend list or not
        let editFriendList = this.dataStore.get(EDIT_STATE);
        let loginForm = document.getElementById("login-form");
        let signupForm = document.getElementById("user-sign-up");
        let userInfo = document.getElementById("user-information");
        let existingUserId = document.getElementById("existing-userId");
        let existingUsername = document.getElementById("existing-username");
        let editFriendForm = document.getElementById("edit-friends-form");
        let userFriendList = document.getElementById("existing-friendsList");
        let noUserTitle = document.getElementById("no-user-title");
        let userTitle = document.getElementById("user-title");

        if (userPageDisplay == LOGIN) {
            loginForm.classList.add("active");
            signupForm.classList.remove("active");
            userInfo.classList.remove("active");
            noUserTitle.classList.add("active");
            userTitle.classList.remove("active");
        } else if (userPageDisplay == SIGN_UP) {
            loginForm.classList.remove("active");
            signupForm.classList.add("active");
            userInfo.classList.remove("active");
            noUserTitle.classList.add("active");
            userTitle.classList.remove("active");

        } else if (userPageDisplay == USER_INFO) {
            this.user = this.dataStore.get(USER);
            existingUserId.textContent = `${this.user.userId}`;
            existingUsername.textContent = `${this.user.userName}`;
            loginForm.classList.remove("active");
            signupForm.classList.remove("active");
            noUserTitle.classList.remove("active");
            userTitle.classList.add("active");
            userFriendList.innerHTML = "";
            if (this.user.friendsList.length == 0) {
                userFriendList.textContent = "No friends yet :(";
            } else {
                userFriendList.innerHTML += `<ul>`;
                this.user.friendsList.forEach(friendId => {userFriendList.innerHTML += `<li>${friendId}</li>`
                });
                userFriendList.innerHTML += `</ul>`;
            }
            userInfo.classList.add("active");

            if (editFriendList == EDIT_NO) {
                editFriendForm.classList.remove("active");
            } else {
                editFriendForm.classList.add("active");
            }
        }
    }
    // Event Handlers --------------------------------------------------------------------------------------------------

    async onUserLogin(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        // get the userId from the input
        let userId = document.getElementById("userId-existing-input").value;
        const userFound = await this.client.findUser(userId, this.userNotFoundErrorHandler);
        // save the user to the datastore
        if (userFound) {
            this.dataStore.setState({
                [USER]:userFound,
                [DISPLAY_STATE]:USER_INFO,
                [LOGIN_STATUS]:LOGIN_SUCCESS});
        } else {
            this.dataStore.set(DISPLAY_STATE, SIGN_UP);
        }
    }

    async onRequestSignUp(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        this.dataStore.set(DISPLAY_STATE, SIGN_UP);
    }

    async onUserSignUp(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        let newUserId = document.getElementById("userId-new-input").value;
        let newUsername = document.getElementById("username-new-input").value;
        const newUser = await this.client.addNewUser(
            newUserId,
            newUsername,
            this.errorHandler);
        this.dataStore.setState({
            [USER]:newUser,
            [DISPLAY_STATE]:USER_INFO,
            });
    }

   async onRequestEditFriends(event) {
    // Prevent the page from refreshing on form submit
        event.preventDefault();
        let editFriendForm = document.getElementById("edit-friends-form");
        editFriendForm.reset();
        this.dataStore.set(EDIT_STATE, EDIT_YES);
    }

   async onAddFriend(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        let friendId = document.getElementById("friends-user-id").value;
        const updatedUser = await this.client.addFriend(
            this.user.userId,
            friendId,
            this.errorHandler);
        this.dataStore.setState({
            [USER]:updatedUser,
            [EDIT_STATE]:EDIT_NO});
    }

    async onRemoveFriend(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        let friendId = document.getElementById("friends-user-id").value;
        const updatedUser = await this.client.removeFriend(
            this.user.userId,
            friendId,
            this.errorHandler);
        this.dataStore.setState({
            [USER]:updatedUser,
            [EDIT_STATE]:EDIT_NO});
    }

//    async onLogout(event) {
//        event.preventDefault();
//        this.dataStore.remove(USER);
//        this.dataStore.remove(USER_ID);
//        this.dataStore.remove(USERS_SUMMARY);
//        document.location = "userLogin.html";
//   }

    userNotFoundErrorHandler(message, error) {
        if (error.response.status == 404) {
            this.dataStore.set("userPageDisplay", "signup");
            return;
        } else {
            this.errorHandler(message)
        }
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const page = new UserLoginPage();
    page.mount();
};

window.addEventListener('DOMContentLoaded', main);