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
        this.bindClassMethods(['onGet', 'onCreate', 'renderSummary', 'onCreateSummary', 'onGetAllSummariesByDate', 'onGetAllFriendSummaries', 'onGetSummariesByUser'], this);
        this.dataStore = new DataStore();
    }

    /**
     * Once the page has loaded, set up the event handlers and fetch the summary list.
     */
    async mount() {
    // @TODO here are event listeners that will detect an action made from html button/field

//        document.getElementById('get-by-id-form').addEventListener('submit', this.onGet);
//        document.getElementById('create-form').addEventListener('submit', this.onCreate);
        this.client = new summaryClient();

        // this will retrieve variables from local storage for use,
        this.firstRender();

        this.dataStore.addChangeListener(this.renderSummary)
    }

    // Render Methods --------------------------------------------------------------------------------------------------


    // @TODO this will populate the resultArea element with a formatted summary entry/post
    async renderSummary(summary) {
       let resultArea = document.getElementById("result-info");

       let summaryEntry = "";

       if (summary != null) {
           summaryEntry += `<ul>`;
           summaryEntry += `<p><h3 class="userName">${summary.username}</h3></p>`;
           summaryEntry += `<p><b>date: </b>${summary.date}</p>`;

           if (summary.results != null) {
               summaryEntry += `<p><b>Score Report: </b>${summary.results}</p>`;
           }

           summaryEntry += `<hr></hr>`;
           summaryEntry += `<p></p>`;
           summaryEntry += `</ul>`;

           resultArea.innerHTML = summaryEntry;
       }
    }

    async firstRender() {
           let userId = dataStore.getItem("userId");

           const review = await this.client.findReview(restaurantId, userId, this.errorHandler());

           await this.renderReview(review);
    }

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
           this.showMessage(`Score posted for ${createdSummary.game}!`);
            await this.renderSummary(result);
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
           this.showMessage(`Got all game scores for ${result.date}!`)
           await this.renderSummary(result);
       } else {
           this.errorHandler("Error retrieving game scores!  Try again...");
           resultArea.innerHTML = "No summaries available";
       }
   }

   async onGetAllFriendSummaries(event) {
       // Prevent the page from refreshing on form submit
       event.preventDefault();

       let year = document.getElementById("filter-friend-summary-year").value;
       let month = document.getElementById("get-friend-summary-month").value;
       let day = document.getElementById("get-friend-summary-day").value;
       let summaryDate = year + "-" + month + "-" + day;

//       let userId = document.getElementById("userId");
       let userId = dataStore.get("userId");

       let result = await this.client.findAllSummariesForUserFriends(summaryDate, userId, errorCallback);

// todo check if the results is JSON or not - jsonify if needed
       this.dataStore.set("friendSummaries-"+summaryDate, result);
       /*result.date = summaryDate;

       if (result) {
            this.showMessage(`Here are your friends game scores for ${result.date}!`)
            await this.renderSummary(result);
       } else {
            this.errorHandler("Error retrieving your friends' game scores! Try again...")
            resultArea.innerHTML = "No summaries available";
       }*/
   }

   async onGetSummariesByUser(event) {
       // Prevent the page from refreshing on form submit
       event.preventDefault();

       let userId = document.getElementById("filter-summary-user");

       let result = await this.client.findAllSummariesForUser(userId, errorCallback);

       // todo check if the results is JSON or not - jsonify if needed

       this.dataStore.set("userSummaries", result);
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
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const summaryPage = new SummaryPage();
    await summaryPage.mount();
};

window.addEventListener('DOMContentLoaded', main);
