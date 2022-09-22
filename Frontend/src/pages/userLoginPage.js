import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import ExampleClient from "../api/exampleClient";

/**
 * Logic needed for the user to log in or sign up.
 */
class UserLoginPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['onUserLogin', 'onRequestSignUp', 'onUserSignUp', 'render'], this);
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
        // note:
        // this line gets the restaurant ID from the URL so it isn't up to the user to type that in
        // this.restaurantId = new URLSearchParams(document.location.search).get("restaurant");
        this.client = new ExampleClient();
        // initial/default state is to display the login form
        this.dataStore.set("display", "login")
        // re-render whenever change made to datastore
        this.dataStore.addChangeListener(this.render);
        this.render();
    }
    // Render Methods -------------------------------------------------------------------------------------------------

    async render() {
        // display signifies the state of the page -> login/signup/userInfo
        let display = this.dataStore.get("display");
        let loginForm = document.getElementById("login-form");
        let signupForm = document.getElementById("user-sign-up");
        let userInfo = document.getElementById("user-information-container");
        let currentUser = null;

        if (display == "login") {
            loginForm.classList.add("active");
            signupForm.classList.remove("active");
            userInfo.classList.remove("active");
        } else if (display == "signup") {
            loginForm.classList.remove("active");
            signupForm.classList.add("active");
            userInfo.classList.remove("active");  

        } else if (display == "userInfo") {
            loginForm.classList.remove("active");
            signupForm.classList.remove("active");

            currentUser = this.dataStore.get("user");
            userInfo.innerHTML = "";
            // add in div containing existing user info
            userInfo.innerHTML += `
            <div class="state">
                <p><strong>User ID</strong>: <span id="existing-userId">${currentUser.userId}</span></p>
                <p><strong>Username</strong>: <span id="existing-username">${currentUser.username}</span></p>
                <p><strong>Friends</strong>: <ul>`;
            currentUser.friendList.forEach(friendId => {userInfo.innerHTML += `<li>${friendId}</li>`             
            });
            userInfo.innerHTML += `</div>`;
            userInfo.classList.add("active"); 
        }
 
    }

    // Event Handlers --------------------------------------------------------------------------------------------------

    async onUserLogin(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();
        // get the userId from the input
        let userId = document.getElementById("userId-input").value;
        // use that userId to make a call to get the user
        const user = await this.client.getUser(userId, this.errorHandler);
        // save the user to the datastore
        this.datastore.setState({"user":user, "display":"userInfo"});
    }
    /*
    get user method for client
    async getUser(userId, errorCallback=console.error) {
        try {
            const response = await this.client.get(`${this.host}/user/${userId}`);
            return response.data;
        } catch (error) {
            this.handleError("getUser", error, errorCallback)
        }
    }
    */

    async onRequestSignUp(event) {
        // Prevent the page from refreshing on form submit
        event.preventDefault();

        this.dataStore.set("display", "signup");
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

        this.dataStore.setState({"user":newUser, "display":"userInfo"});

        // note:
        // go back to a particular URL / is the root page    like cd in gitbash
        // document.location = "/";
    }
    /*
    client method to add new user
     async addNewUser(userId, username, errorCallback=console.error) {
        try {
           const response = await this.client.post(`${this.host}/user/`, {
               "userId": userId,
               "username": username,
           });
           return response.data;
       } catch (error) {
           this.handleError("addNewUser", error, errorCallback);
       }
   }
    */

}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const page = new EditReviewsPage();
    page.mount();
};

window.addEventListener('DOMContentLoaded', main);