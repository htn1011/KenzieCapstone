import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import summaryClient from "../api/summaryClient";

// display states
const viewDisplay = "view";
const editDisplay = "edit";


/**
 * Logic needed for the view playlist page of the website.
 */

class EditSummaryPage extends BaseClass {


    constructor() {
        super();
       this.bindClassMethods(['render', 'onRequestEdit', 'onEdit', 'onDelete'], this);
        this.dataStore = new DataStore();
        // only one summary used while on this page and deleted once edit is complete
        this.summary =  this.dataStore.get("updateSummary");
        this.user = this.dataStore.get("user");

    }

    /**
     * Once the page has loaded, set up the event handlers and fetch the concert list.
     */
    async mount() {
       document.getElementById('edit-summary-selection').addEventListener('click', this.onRequestEdit);
       document.getElementById('delete-button-selection').addEventListener('click', this.onDelete);
       document.getElementById('edit-summary-form').addEventListener('submit', this.onEdit);
        this.client = new summaryClient();
        //initial state view and only updated when edit button is pressed
        this.dataStore.set("display", viewDisplay);


        this.dataStore.addChangeListener(this.render)
        this.render();
    }

    // Render Methods --------------------------------------------------------------------------------------------------

   async render() {
    // get all required elements
        let editForm = document.getElementById("edit-summary-form");
        let username = document.getElementById("existing-username");
        let userId = document.getElementById("existing-userId");
        let gameName = document.getElementById("game-name");
        let summaryDate = document.getElementById("summary-date");
        let summaryResults = document.getElementById("summary-results");

    // fill out info on page
        username.textContent = this.user.userName;
        userId.textContent = this.user.userId;
        gameName.textContent = this.summary.game;
        summaryDate.textContent = this.summary.date;
        summaryResults.textContent = this.summary.results;

    //get state from datastore
        let display = this.dataStore.get("display");
    // render according to
        if (display == viewDisplay) {
            editForm.classList.remove("active");
        } else if (display == editDisplay) {
            editForm.classList.add("active");
        }

   }

    // Event Handlers --------------------------------------------------------------------------------------------------

   async onRequestEdit(event) {
        event.preventDefault();
        this.dataStore.set("display", editDisplay);
   }

   async onEdit(event) {
        event.preventDefault();
        let guesses = document.getElementById("edit-summary-guesses").value;
        let comments = document.getElementById("edit-summary-description").value;
        let updatedResults = guesses + " " + comments;
        //
        let updatedSummary = await this.client.updateGameSummary(this.summary, updatedResults, this.ErrorHandler);
        if (updatedSummary) {
            this.showMessage(`You have updated your ${this.summary.game} summary results for ${this.summary.date} from
            ${this.summary.results} to ${updatedResults}`);
        }
        this.dataStore.remove("updateSummary");
        document.location = "summary.html";

   }

   async onDelete(event) {
        event.preventDefault();
        await this.client.deleteSummaryBySummaryId(this.summary.date, this.summary.userId, this.ErrorHandler);
        this.showMessage(`You have deleted your ${this.summary.game} summary for ${this.summary.date}`);
        this.dataStore.remove("updateSummary");
        this.dataStore.remove("userSummary");
        document.location = "summary.html";
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const editSummaryPage = new EditSummaryPage();
    editSummaryPage.mount();
};

window.addEventListener('DOMContentLoaded', main);
