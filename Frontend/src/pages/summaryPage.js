import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import summaryClient from "../api/summaryClient";

// display states
const LOGIN_STATUS = "loginStatus";
const FILTER_TYPE = "currentListFilter";
// display state variations
const TODAYS_DATE_FILTER = "Results from today:";
const DATE_FILTER = "Results from the date you selected:";
const FRIENDS_FILTER = "Results from only your friends:";
const USER_FILTER = "Results your selected user:";
const ONLY_MINE = "Only your results:"
const LOGIN_NEEDED = "login needed";
const LOGIN_SUCCESS = "success";
// attribute names for datastore
const GAME = "game";
const USER = "user";
const USER_ID = "userId";
const TODAYS_DATE = "todaysDate";
const USERS_SUMMARY = "userSummary";
const LIST_OF_SUMMARIES = "listOfSummaries";
const UPDATE_SUMMARY = "updateSummary"
// const variables
const GAME_NAME = "wordle";

/**
 * Logic needed for the view summaries leaderboard page of the website.
 */
class SummaryPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['render','renderSummaryList', 'onCreateSummary', 'onGetAllSummariesByDate',
        'onGetAllFriendSummaries', 'onGetSummariesByUser', 'onGetSummariesByOtherUser', 'onLogout', 'onLogin',
        'onRequestEdit'], this);
        this.dataStore = new DataStore();
    }

    /**
     * Once the page has loaded, set up the event handlers and fetch the summary list.
     */
    async mount() {
        // Listeners
        document.getElementById('create-summary-form').addEventListener('submit', this.onCreateSummary);
        document.getElementById('get-summaries-by-date-form').addEventListener('submit', this.onGetAllSummariesByDate);
        document.getElementById('get-friend-summaries').addEventListener('click', this.onGetAllFriendSummaries);
        document.getElementById('filter-only-user').addEventListener('click', this.onGetSummariesByUser);
        document.getElementById('summary-logout').addEventListener('click', this.onLogout);
        document.getElementById('summary-login').addEventListener('click', this.onLogin);
        document.getElementById('posted-edit-button').addEventListener('click', this.onRequestEdit);

        this.client = new summaryClient();
        // default list filter and game type
        this.dataStore.set(FILTER_TYPE, TODAYS_DATE_FILTER);
        this.dataStore.set(GAME, GAME_NAME);
        //get today's date
        // https://www.tutorialrepublic.com/faq/how-to-format-javascript-date-as-yyyy-mm-dd.php
        let today = new Date();
        let year = today.toLocaleString("default", { year: "numeric" });
        let month = today.toLocaleString("default", { month: "2-digit" });
        let day = today.toLocaleString("default", { day: "2-digit" });
        let formattedDate = year + "-" + month + "-" + day;
        this.dataStore.set(TODAYS_DATE, formattedDate);
        // check if there is a user or not->log in state if not
        this.user = this.dataStore.get(USER);
        if (this.user == undefined) {
            this.dataStore.set(LOGIN_STATUS, LOGIN_NEEDED);
        } else {
            this.dataStore.set(LOGIN_STATUS, LOGIN_SUCCESS);
            // todo check if summary already exists for efficiency improvement
            let userSummary = await this.client.findGameSummaryFromUser(formattedDate, this.user.userId, this.summaryErrorHandler);
            if (userSummary) {
                this.dataStore.set(USERS_SUMMARY, userSummary);
            }
        }
        let initalList = await this.client.findAllSummariesForDate(formattedDate, this.summaryErrorHandler);
        this.dataStore.set(LIST_OF_SUMMARIES, initalList);
        // add change listener
        this.dataStore.addChangeListener(this.render)
        // initial render
        this.render();
    }

    // Render Methods --------------------------------------------------------------------------------------------------
    async render() {
        let logInButton = document.getElementById("button-to-login");
        let userWelcome = document.getElementById("user-welcome");
        let userIdWelcome = document.getElementById("user-id-welcome");
        let usernameWelcome = document.getElementById("user-name-welcome");
        let postNewSummary = document.getElementById("post-new-Summary");
        let friendFiler = document.getElementById("get-friend-summaries");
        let onlyMeFilter = document.getElementById("filter-only-user");
        let todaysDateContainer = document.getElementById("todays-date");
        let postSummaryForm = document.getElementById("create-summary-form");
        let summaryPosted = document.getElementById("summary-posted");
        let postedSummaryDate = document.getElementById("posted-summary-date");
        let postedSummaryResults = document.getElementById("posted-summary-results");
        let postedSummaryButton = document.getElementById("posted-edit-button");
        let backToTodayButton = document.getElementById("refresh-today");
        let today = this.dataStore.get(TODAYS_DATE);
        // check display state and render according to state
        let loginStatus = this.dataStore.get(LOGIN_STATUS);
        if (loginStatus == LOGIN_NEEDED) {
            // is login is required - only show that day's results and request login for further functionality
            logInButton.classList.add("active");
            userWelcome.classList.remove("active");
            postNewSummary.classList.remove("active");
            friendFiler.classList.remove("active");
            onlyMeFilter.classList.remove("active");
            backToTodayButton.classList.remove("active");
        } else if (loginStatus == LOGIN_SUCCESS) {
            // if there is a logged in user
            this.user = this.dataStore.get(USER);
            todaysDateContainer.textContent = today;
            let usersSummary = this.dataStore.get(USERS_SUMMARY);
            if (usersSummary && usersSummary.date == today) {
                // if there is already a summary from today show that
                postSummaryForm.classList.remove("active");
                summaryPosted.classList.add("active");
                postedSummaryDate.textContent = usersSummary.date;
                postedSummaryResults.textContent = usersSummary.results;
                postedSummaryButton.dataset.date = usersSummary.date;
            } else {
                // allow user to post a new summary
                postSummaryForm.classList.add("active");
                summaryPosted.classList.remove("active");
            }
            // remove the change the buttons and show the filters
            logInButton.classList.remove("active");
            userWelcome.classList.add("active");
            userIdWelcome.textContent = this.user.userId;
            usernameWelcome.textContent = this.user.userName;
            postNewSummary.classList.add("active");
            friendFiler.classList.add("active");
            onlyMeFilter.classList.add("active");
            backToTodayButton.classList.add("active");
        }
        // get the filter type and the list of summaries
        let listFilter = this.dataStore.get(FILTER_TYPE);
        let summaryList = this.dataStore.get(LIST_OF_SUMMARIES);
        let summaryListType = document.getElementById("summary-list-filter-type");
        summaryListType.innerText = listFilter;
        // render the summaries
        this.renderSummaryList(summaryList);
    }

    async renderSummaryList(summaryList) {
         let resultArea = document.getElementById("summary-list-container");
         resultArea.innerHTML = "";
         // if there are no summaries
         if (summaryList == null || summaryList.length == 0) {
              resultArea.textContent = "There are no results to display";
              return;
         }
         // render the summaries into a list
         summaryList.forEach(summary => {
             const li = document.createElement("div");
             let liContent = `<div class="card" id="oneSummary">`;
             // consider wrapping strong around span instead - KK
             liContent += `<p>User ID: <strong><span id="summary-userId" style="font-size: 16pt">${summary.userId}</span></strong></p>`;
             liContent += `<p>Game Date: <strong><span id="summary-date" style="font-size: 16pt">${summary.date}</span></strong></p>`;
             liContent += `<p>Results: <strong><span id="summary-results" style="font-size: 16pt">${summary.results}</span></strong></p>`;
             if (this.user && summary.userId == this.user.userId) {
                 liContent += `<button type="button" data-date="${summary.date}">Edit</button>`;
                 liContent += `</div>`;
             } else {
                 liContent += `<button type="button" data-userid="${summary.userId}">View this user's results</button>`;
                 liContent += `</div>`;
             }
             li.innerHTML = liContent;
              if (this.user && summary.userId == this.user.userId) {
                 li.querySelector("button").addEventListener('click', this.onRequestEdit);
              } else {
                 li.querySelector("button").addEventListener('click', this.onGetSummariesByOtherUser);
              }
             resultArea.append(li);
         });
         resultArea.classList.add("active");
    }
     async renderSummary(summary, container) {
         container.innerHTML = `<p>Your score for ${summary.date} is: ${summary.results}</p>`;
         container.innerHTML += `<button type="button" data-date="${summary.date}">Edit</button>`;
         container.querySelector("button").addEventListener('click', this.onRequestEdit);
     }

    // Event Handlers --------------------------------------------------------------------------------------------------
    async onRequestEdit(event) {
         event.preventDefault();
         // get the date from the request
         let date = event.target.dataset.date;
         // get the existing summary that needs to be updated
         let summary = await this.client.findGameSummaryFromUser(date, this.user.userId, this.summaryErrorHandler);
         // save that summary to be updated
         this.dataStore.setSilent(UPDATE_SUMMARY, summary);
         // go to the edit page
         document.location = "editSummary.html";
    }

    async onCreateSummary(event) {
         // Prevent the page from refreshing on form submit
         event.preventDefault();
         let createSummaryButton = document.getElementById('createSummaryButton');
         createSummaryButton.innerText = 'posting...';
         createSummaryButton.disabled = true;
         let game = this.dataStore.get(GAME);
         // for now/while the game is wordle session ID == date
         let sessionNumber = this.dataStore.get(TODAYS_DATE);
         // retrieve results from user input in field

         // KK: guess vs guesses
         let resultsRaw = document.getElementById("create-summary-guesses").value;

         let results;

         if (resultsRaw.charAt(0) === '1') {
             results = document.getElementById("create-summary-guesses").value + " guess; "
                 + document.getElementById("create-summary-description").value;
         } else {
             results = document.getElementById("create-summary-guesses").value + " guesses; "
                 + document.getElementById("create-summary-description").value;
         }

         const createdSummary = await this.client.postNewSummary(game, this.user.userId, sessionNumber, results, this.errorHandler);
         if (createdSummary) {
             this.showMessage(`Score posted for today's ${createdSummary.game}!`);
             let todaysDate = this.dataStore.get(TODAYS_DATE);
             let result = await this.client.findAllSummariesForDate(todaysDate, this.errorHandler);
             this.dataStore.setState({
                [FILTER_TYPE]:TODAYS_DATE_FILTER,
                [LIST_OF_SUMMARIES]:result,
                [USERS_SUMMARY]:createdSummary});
         } else {
             this.errorHandler("Error posting!  Try again...");
         }
    }

   async onGetAllSummariesByDate(event) {
         // Prevent the page from refreshing on form submit
         event.preventDefault();
         // get the date to filter by and format properly
         let year = document.getElementById("filter-summary-year").value;
         let month = document.getElementById("get-summary-month").value;
         let day = document.getElementById("get-summary-day").value;
         let date = year + "-" + month + "-" + day;
         let result = await this.client.findAllSummariesForDate(date, this.errorHandler);
         this.showMessage(`Got all game scores for ${date}!`);
         // update state and values
         this.dataStore.setState({
            [FILTER_TYPE]:DATE_FILTER,
            [LIST_OF_SUMMARIES]:result});
   }

   async onGetAllFriendSummaries(event) {
         // Prevent the page from refreshing on form submit
         event.preventDefault();
         // get the date to filter by and format properly
         // let year = document.getElementById("filter-friend-summary-year").value;
         // let month = document.getElementById("get-friend-summary-month").value;
         // let day = document.getElementById("get-friend-summary-day").value;
         // let summaryDate = year + "-" + month + "-" + day;
         // let result = await this.client.findAllSummariesForUserFriends(summaryDate, this.user.userId, this.errorHandler);
         let year = document.getElementById("filter-summary-year").value;
         let month = document.getElementById("get-summary-month").value;
         let day = document.getElementById("get-summary-day").value;
         let date = year + "-" + month + "-" + day;
         let result = await this.client.findAllSummariesForUserFriends(date, this.user.userId, this.errorHandler);

         this.showMessage(`Got all your friend's game scores for ${date}!`);
         // update state and values
         this.dataStore.setState({
            [FILTER_TYPE]:FRIENDS_FILTER,
            [LIST_OF_SUMMARIES]:result});
   }

   async onGetSummariesByOtherUser(event) {
         event.preventDefault();
         // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dataset
         let userId = event.target.dataset.userid;

         let result = await this.client.findAllSummariesForUser(userId, this.errorHandler);
         this.showMessage(`Got all your game scores!`);
         // update state and values
         this.dataStore.setState({
            [FILTER_TYPE]:USER_FILTER,
            [LIST_OF_SUMMARIES]:result});
   }

   async onGetSummariesByUser(event) {
         // Prevent the page from refreshing on form submit
         event.preventDefault();
         let result = await this.client.findAllSummariesForUser(this.user.userId, this.errorHandler);
         this.showMessage(`Got all your game scores!`);
         // update state and values
         this.dataStore.setState({
            [FILTER_TYPE]:ONLY_MINE,
            [LIST_OF_SUMMARIES]:result});
   }

   async onLogout(event) {
         // Prevent the page from refreshing on form submit
         event.preventDefault();
         this.dataStore.remove(USER);
         this.dataStore.remove(USER_ID);
         this.dataStore.remove(USERS_SUMMARY);
         document.location = "summary.html";
   }

   async onLogin(event) {
         // Prevent the page from refreshing on form submit
         event.preventDefault();
         document.location = "userLogin.html";
   }

    summaryErrorHandler(message, error) {
         console.dir(error);
         if (error.response.status == 404) {
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
    const summaryPage = new SummaryPage();
    await summaryPage.mount();
};

window.addEventListener('DOMContentLoaded', main);
