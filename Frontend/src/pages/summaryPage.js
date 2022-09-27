import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import ExampleClient from "../api/exampleClient";
import summaryClient from "../api/summaryClient";

/**
 * Logic needed for the view summaries leaderboard page of the website.
 */
class SummaryPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['render','renderSummaryList', 'onCreateSummary', 'onGetAllSummariesByDate',
        'onGetAllFriendSummaries', 'onGetSummariesByUser', 'onGetSummariesByOtherUser'], this);
        this.dataStore = new DataStore();
    }

    /**
     * Once the page has loaded, set up the event handlers and fetch the summary list.
     */
    async mount() {
    // @TODO here are event listeners that will detect an action made from html button/field

//        document.getElementById('get-by-id-form').addEventListener('submit', this.onGet);
//        document.getElementById('create-form').addEventListener('submit', this.onCreate);
        document.getElementById('create-summary-form').addEventListener('submit', this.onCreateSummary);
        document.getElementById('get-summaries-by-date-form').addEventListener('submit', this.onGetAllSummariesByDate);
        document.getElementById('get-friend-summaries').addEventListener('submit', this.onGetAllFriendSummaries);
        document.getElementById('filter-only-user').addEventListener('click', this.onGetSummariesByUser);
        this.client = new summaryClient();

        // this will retrieve variables from local storage for use,
//        this.firstRender();
       // todo state to show if user is logged in or not
       let user = this.dataStore.get("user");
        if (user == null) {
            this.dataStore.set("loginStatus", "login needed");
        } else {
            this.dataStore.set("loginStatus", "success");
        }




        // when initally loading this page, the default list today's and yesterdays summaries
        // altering the whichList state can determine which list to render
        // options: todayAndYesterday, byDate, friends, onlyMine
        this.dataStore.set("currentListFilter", "Today's Results");
        // https://www.tutorialrepublic.com/faq/how-to-format-javascript-date-as-yyyy-mm-dd.php
        let today = new Date();
        let year = today.toLocaleString("default", { year: "numeric" });
        let month = today.toLocaleString("default", { month: "2-digit" });
        let day = today.toLocaleString("default", { day: "2-digit" });
        let formattedDate = year + "-" + month + "-" + day;
        this.dataStore.set("todaysDate", formattedDate);

        let initalList = await this.client.findAllSummariesForDate(formattedDate, this.summaryErrorHandler);
        this.dataStore.set("listOfSummaries", initalList);

        this.dataStore.addChangeListener(this.render)
        this.render();
    }

    // Render Methods --------------------------------------------------------------------------------------------------



    // @TODO this will populate the resultArea element with a formatted summary entry/post/list
    async render() {
        let logInButton = document.getElementById("link-to-login");
        let userWelcome = document.getElementById("user-welcome");
        let userIdWelcome = document.getElementById("user-id-welcome");
        let usernameWelcome = document.getElementById("user-name-welcome");
        let postNewSummary = document.getElementById("post-new-Summary");
        let friendFiler = document.getElementById("friend-filer");
        let onlyMeFilter = document.getElementById("only-me-filter");

        let loginStatus = this.dataStore.get("loginStatus");
        if (loginStatus == "login needed") {
            logInButton.classList.add("active");
            userWelcome.classList.remove("active");
            postNewSummary.classList.remove("active");
            friendFiler.classList.remove("active");
            onlyMeFilter.classList.remove("active");
        } else if (loginStatus == "success") {
            let user = this.dataStore.get("user");
            logInButton.classList.remove("active");
            userWelcome.classList.add("active");
            userIdWelcome.textContent = user.userId;
            usernameWelcome.textContent = user.userName;
            postNewSummary.classList.add("active");
            friendFiler.classList.add("active");
            onlyMeFilter.classList.add("active");
        }


        let listFilter = this.dataStore.get("currentListFilter");
        let summaryList = this.dataStore.get("listOfSummaries");

        let summaryListType = document.getElementById("summary-list-filter-type");
        summaryListType.innerText = listFilter;
        this.renderSummaryList(summaryList);

// Use new Date() to generate a new Date object containing the current date and time.
// This will give you today's date in the format of mm/dd/yyyy.
// Simply change today = mm +'/'+ dd +'/'+ yyyy; to whatever format you wish
// example:
// var today = new Date();
// var dd = String(today.getDate()).padStart(2, '0');
// var mm = String(today.getMonth() + 1).padStart(2, '0'); // January is 0!
// var yyyy = today.getFullYear();
// today = mm + '/' + dd + '/' + yyyy;
// document.write(today);

//        if (listFilter == "Today's and Yesterday's Results") {
//            summaryListType.innerText = "";
//        } else if (listFilter == "by User") {
//            summaryListType.innerText = "";
//        } else if (listFilter == "by Date") {
//            summaryListType.innerText = "";
//        } else if (listFilter == "by Friends") {
//            summaryListType.innerText = "";
//        }

    }

    async renderSummaryList(summaryList) {
           let resultArea = document.getElementById("summary-list-container");
           // print label of current rendered list
//           let summaryListType = document.getElementById("summary-list-filter-type");
//           summaryListType.innerText = dataStore.get("currentListFilter");

           resultArea.innerHTML = "";
           if (summaryList == null) {
                resultArea.textContent = "There are no results to display";
                return;
           }

           const ul = document.createElement("ul");

           summaryList.forEach(summary => {
               const li = document.createElement("li");
               li.innerHTML += `<div class="card">`;
                // @TODO consider retrieving user name instead of userId?
               li.innerHTML += `<p><strong>User ID</strong>: <span id="summary-userId">${summary.userId}</span></p>`;
               li.innerHTML += `<p><strong>Results</strong>: <span id="summary-results">${summary.results}</span></p>`;
               li.innerHTML += `<button type="button" data-userid="${summary.userId}">View this user's results</button>`;
               li.innerHTML += `</div>`;
               li.querySelector("button").addEventListener('click', this.onGetSummariesByOtherUser);
               ul.append(li);
           });
           resultArea.append(ul);
           resultArea.classList.add("active");
    }

//    async firstRender() {
//           let userId = dataStore.getItem("userId");
//
//           const review = await this.client.findReview(restaurantId, userId, this.errorHandler());
//
//           await this.renderReview(review);
//    }

    // Event Handlers --------------------------------------------------------------------------------------------------

    /* @TODO when event listeners detect an action input, these handlers will translate information
            from user input and call summaryClient to process this information */

   async onCreateSummary(event) {
       // Prevent the page from refreshing on form submit
       event.preventDefault();

       let createSummaryButton = document.getElementById('createSummaryButton');
       createSummaryButton.innerText = 'posting...';
       createSummaryButton.disabled = true;

//       this.dataStore.set("example", null);

       // retrieve stored info from DataStore
       let game = this.dataStore.get("game");
       let userId = this.dataStore.get("userId");
       let sessionNumber = this.dataStore.get("sessionNumber");
       // retrieve results from user input in field
       let results = document.getElementById("create-summary-guesses").value + " "
        + document.getElementById("create-summary-description").value;

       // input this info into summaryClient method
       const createdSummary = await this.client.postNewSummary(game, userId, sessionNumber, results, this.errorHandler);

       // this.dataStore.set("example", createdSummary.username);

       if (createdSummary) {
           this.showMessage(`Score posted for today's ${createdSummary.game}!`);
           let todaysDate = this.dataStore.get("todaysDate");
           let result = await this.client.findAllSummariesForDate(todaysDate, this.errorHandler);
           this.dataStore.setState({"currentListFilter":"Today's Results", "listOfSummaries":result});

       } else {
           this.errorHandler("Error posting!  Try again...");
       }
   }


   async onGetAllSummariesByDate(event) {
       // Prevent the page from refreshing on form submit
       event.preventDefault();

       // @TODO find use cases for DataStore storage
       // this.dataStore.set("summariesByDate", date);

       let year = document.getElementById("filter-summary-year").value;
       let month = document.getElementById("get-summary-month").value;
       let day = document.getElementById("get-summary-day").value;

       let date = year + "-" + month + "-" + day;

       let result = await this.client.findAllSummariesForDate(date, this.errorHandler);

       result.date = date;

       if (result) {
                  this.showMessage(`Got all game scores for ${date}!`);
                  this.dataStore.setState({"currentListFilter":"by Date", "listOfSummaries":result});
//                  await this.renderSummaryList(result);
              } else {
//                  this.errorHandler("Error retrieving game scores!  Try again...");
                  resultArea.innerHTML = "No summaries available";
              }

       // for labeling the rendered list
//       dataStore.set("currentListFilter", "by Date");


   }

   async onGetAllFriendSummaries(event) {
       // Prevent the page from refreshing on form submit
       event.preventDefault();

       // todo add in functionality to get and include own summary in this list
       let includeMe = document.getElementById("filter-only-friends-me").value;
       let year = document.getElementById("filter-friend-summary-year").value;
       let month = document.getElementById("get-friend-summary-month").value;
       let day = document.getElementById("get-friend-summary-day").value;
       let summaryDate = year + "-" + month + "-" + day;

//       let userId = document.getElementById("userId");
       let userId = dataStore.get("userId");

       let result = await this.client.findAllSummariesForUserFriends(summaryDate, userId, errorCallback);

       // for labeling the rendered list
//       this.dataStore.setState({"currentListFilter":"by Friends", "listOfSummaries":result});
//       dataStore.set("currentListFilter", "by Friends");

       if (result) {
            this.showMessage(`Got all your friend's game scores for ${summaryDate}!`);
            this.dataStore.setState({"currentListFilter":"by Friends", "listOfSummaries":result});
//                  await this.renderSummaryList(result);
       } else {
//                  this.errorHandler("Error retrieving game scores!  Try again...");
            resultArea.innerHTML = "No summaries available";
       }

// todo check if the results is JSON or not - jsonify if needed
//       this.dataStore.set("friendSummaries-"+summaryDate, result);
       /*result.date = summaryDate;

       if (result) {
            this.showMessage(`Here are your friends game scores for ${result.date}!`)
            await this.renderSummary(result);
       } else {
            this.errorHandler("Error retrieving your friends' game scores! Try again...")
            resultArea.innerHTML = "No summaries available";
       }*/
   }

   async onGetSummariesByOtherUser(event) {
         event.preventDefault();
         // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dataset
         console.log(event.target.dataset);
         let userId = event.target.dataset.userid;

         let result = await this.client.findAllSummariesForUser(userId, errorCallback);
        if (result) {
                    this.showMessage(`Got all your game scores!`);
                    this.dataStore.setState({"currentListFilter":"by User", "listOfSummaries":result});
        //                  await this.renderSummaryList(result);
        } else {
        //                  this.errorHandler("Error retrieving game scores!  Try again...");
                    resultArea.innerHTML = "No summaries available";
        }

   }

   async onGetSummariesByUser(event) {
       // Prevent the page from refreshing on form submit
       event.preventDefault();

       let userId = this.dataStore.get("userId");

       let result = await this.client.findAllSummariesForUser(userId, errorCallback);

       // for labeling the rendered list
//       dataStore.set("currentListFilter", "by User");

       // todo check if the results is JSON or not - jsonify if needed

//       this.dataStore.set("userSummaries", result);
       this.dataStore.setState({"currentListFilter":"by User", "listOfSummaries":result});

       if (result) {
                   this.showMessage(`Got all your game scores!`);
                   this.dataStore.setState({"currentListFilter":"by User", "listOfSummaries":result});
       //                  await this.renderSummaryList(result);
       } else {
       //                  this.errorHandler("Error retrieving game scores!  Try again...");
                   resultArea.innerHTML = "No summaries available";
       }
   }

//    async onGet(event) {
//        // Prevent the page from refreshing on form submit
//        event.preventDefault();
//
//        let id = document.getElementById("id-field").value;
//        this.dataStore.set("example", null);
//
//        let result = await this.client.getExample(id, this.errorHandler);
//        this.dataStore.set("example", result);
//        if (result) {
//            this.showMessage(`Got ${result.name}!`)
//        } else {
//            this.errorHandler("Error doing GET!  Try again...");
//        }
//    }

//    async onCreate(event) {
//        // Prevent the page from refreshing on form submit
//        event.preventDefault();
//        this.dataStore.set("example", null);
//
//        let name = document.getElementById("create-name-field").value;
//
//        const createdExample = await this.client.createExample(name, this.errorHandler);
//        this.dataStore.set("example", createdExample);
//
//        if (createdExample) {
//            this.showMessage(`Created ${createdExample.name}!`)
//        } else {
//            this.errorHandler("Error creating!  Try again...");
//        }
//    }
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
