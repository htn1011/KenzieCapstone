import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import SummaryClient from "../api/summaryClient";

/**
 * Logic needed for the user to log in or sign up.
 */
class UserLoginPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['onUserLogin', 'onRequestSignUp', 'onUserSignUp', 'render', 'onRemoveFriend', 'onRequestEditFriends', 'onAddFriend'], this);
        this.dataStore = new DataStore();

    }

    /**
     * Once the page has loaded, set up the event handlers
     */
    async mount() {
        // event for the login form being submitted
        document.getElementById('login-form').addEventListener('submit', this.onUserLogin);
        // event for request to sign up
        document.getElementById('sign-up-request').addEventListener('click', this.onRequestSignUp);
        // event listener for the sign up form
        document.getElementById('sign-up-form').addEventListener('submit', this.onUserSignUp);
        document.getElementById('edit-friends').addEventListener('click', this.onRequestEditFriends);
        document.getElementById('add-friend').addEventListener('submit', this.onAddFriend);
        document.getElementById('remove-friend').addEventListener('submit', this.onRemoveFriend);
        document.getElementById('user-logout').addEventListener('submit', this.onLogout);
        // note:
        // this line gets the restaurant ID from the URL so it isn't up to the user to type that in
        // this.restaurantId = new URLSearchParams(document.location.search).get("restaurant");
        this.client = new SummaryClient();
        // if the user has already logged in - show user info otherwise login page
        let user = this.dataStore.get("user");
        if (user == null) {
            this.dataStore.set("userPageDisplay", "login");
        } else {
            this.dataStore.set("userPageDisplay", "userInfo");
        }
        // re-render whenever change made to datastore
        this.dataStore.addChangeListener(this.render);
        this.render();
    }
    // Render Methods -------------------------------------------------------------------------------------------------

    async render() {
        // userPageDisplay signifies the state of the page -> login/signup/userInfo
        let userPageDisplay = this.dataStore.get("userPageDisplay");
        // editFriendList signifies the state of whether to edit the friend list or not
        let editFriendList = this.dataStore.get("editFriendList");
        let loginForm = document.getElementById("login-form");
        let signupForm = document.getElementById("user-sign-up");
        let userInfo = document.getElementById("user-information");
        let editFriendForm = document.getElementById("edit-friends-form");
        let userFriendList = document.getElementById("existing-friendsList");

        if (userPageDisplay == "login") {
            loginForm.classList.add("active");
            signupForm.classList.remove("active");
            userInfo.classList.remove("active");
        } else if (userPageDisplay == "signup") {
            loginForm.classList.remove("active");
            signupForm.classList.add("active");
            userInfo.classList.remove("active");

        } else if (userPageDisplay == "userInfo") {
            let currentUser = this.dataStore.get("user");
            loginForm.classList.remove("active");
            signupForm.classList.remove("active");
            userFriendList.innerHTML = "";
            if (currentUser.friendList.length == 0) {
                userFriendList.textContent = "No friends yet :(";
            } else {
                userFriendList.innerHTML += `<ul>`;
                currentUser.friendList.forEach(friendId => {userFriendList.innerHTML += `<li>${friendId}</li>`
                });
                userFriendList.innerHTML += `</ul>`;
            }
            userInfo.classList.add("active");

            if (editFriendList == no) {
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

        // store userId in local storage for use in summaryPage.js
        this.dataStore.set("userId", userId);

        // use that userId to make a call to get the user
        const user = await this.client.findUser(userId, this.errorHandler);
        // save the user to the datastore
        this.datastore.setState({"user":user, "userPageDisplay":"userInfo"});
    }

    async onRequestSignUp(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();

        this.dataStore.set("userPageDisplay", "signup");
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

        this.dataStore.set("userId", newUserId);
        console.log(newUser);

        this.dataStore.setState({"user":newUser, "userPageDisplay":"userInfo"});

        // note:
        // go back to a particular URL / is the root page    like cd in gitbash
        // document.location = "/";
    }

   async onRequestEditFriends(event) {
    // Prevent the page from refreshing on form submit
    event.preventDefault();

    this.dataStore.set("editFriendList", "yes");
    }

   async onAddFriend(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();

        let userId = this.dataStore.get("user").userId;
        let friendId = document.getElementById("friends-user-id").value;

        const updatedUser = await this.client.addfriend(
            userId,
            friendId,
            this.errorHandler);

        this.dataStore.setState({"user":updatedUser, "editFriendList":"no"});
    }

    async onRemoveFriend(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();

        let userId = this.dataStore.get("user").userId;
        let friendId = document.getElementById("friends-user-id").value;

        const updatedUser = await this.client.removeFriend(
            userId,
            friendId,
            this.errorHandler);

        this.dataStore.setState({"user":updatedUser, "editFriendList":"no"});
    }

    async onLogout(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        this.dataStore.clear();
        document.location = "/";
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