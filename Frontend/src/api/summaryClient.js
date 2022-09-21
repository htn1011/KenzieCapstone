import BaseClass from "../util/baseClass";
import axios from 'axios'

// @TODO: this client will call the controller methods by accessing the endpoints defined in controller


export default class summaryClient extends BaseClass {

    constructor(props = {}){
        super();
        // @TODO the client must bind all methods that will be used
        const methodsToBind = ['clientLoaded', 'postNewSummary', 'findGameSummaryFromUser', 'updateGameSummary',
            'deleteSummaryBySummaryId', 'findAllSummariesForDate', 'findAllSummariesForUser'];
        this.bindClassMethods(methodsToBind, this);
        this.props = props;
        this.clientLoaded(axios);
    }

    /**
     * Run any functions that are supposed to be called once the client has loaded successfully.
     * @param client The client that has been successfully loaded.
     */
    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty("onReady")){
            this.props.onReady();
        }
    }

    // date does not get input here, is generated in backend - KK
    async postNewSummary(game, userId, sessionNumber, results, errorCallback) {
        try {
            const response = await this.client.post(`/game/wordle`, {
                "game": game,
                "userId": userId,
                "sessionNumber": sessionNumber,
                "results": results
            });
            return response.data;
        } catch (error) {
            this.handleError("postNewSummary", error, errorCallback);
        }
    }

    async findGameSummaryFromUser(summaryDate, userId, errorCallback) {
        try {
            const response = await this.client.get(`/${summaryDate}/${userId}`);
            return response.data;
        } catch (error) {
            this.handleError("findGameSummaryFromUser", error, errorCallback)
        }
    }

    // updateGameSummary
    async updateGameSummary(game, userId, sessionNumber, results, errorCallback) {
        try {
            const response = await this.client.put(`/editSummary`, {
                "game": game,
                "userId": userId,
                "sessionNumber": sessionNumber,
                "results": results
            });
            return response.data;
        } catch (error) {
            this.handleError("updateGameSummary", error, errorCallback);
        }
    }

    // deleteSummaryBySummaryId
    async deleteSummaryBySummaryId(summaryDate, userId, errorCallback) {
        try {
            const response = await this.client.delete(`/${summaryDate}/${userId}`);
            return response.data;
        } catch (error) {
            this.handleError("deleteSummaryBySummaryId", error, errorCallback)
        }
    }

    // findAllSummariesForDate
    async findAllSummariesForDate(summaryDate, errorCallback) {
        try {
            const response = await this.client.get(`/${summaryDate}/all`);
            return response.data;
        } catch (error) {
            this.handleError("findAllSummariesForDate", error, errorCallback)
        }
    }

    // findAllSummariesForUser
    async findAllSummariesForUser(userId, errorCallback) {
        try {
            const response = await this.client.get(`/${userId}/all`);
            return response.data;
        } catch (error) {
            this.handleError("findAllSummariesForUser", error, errorCallback)
        }
    }

    // @TODO determine whether or not we should have user side methods in a separate client -KK

/*
   async getExample(id, errorCallback) {
       try {
           const response = await this.client.get(`/example/${id}`);
           return response.data;
       } catch (error) {
           this.handleError("getConcert", error, errorCallback)
       }
   }

   async createExample(name, errorCallback) {
       try {
           const response = await this.client.post(`example`, {
               name: name
           });
           return response.data;
       } catch (error) {
           this.handleError("createExample", error, errorCallback);
       }
   }

 */

    /**
     * Helper method to log the error and run any error functions.
     * @param error The error received from the server.
     * @param errorCallback (Optional) A function to execute if the call fails.
     */
   handleError(method, error, errorCallback) {
       console.error(method + " failed - " + error);
       if (error.response.data.message !== undefined) {
           console.error(error.response.data.message);
       }
       if (errorCallback) {
           errorCallback(method + " failed - " + error);
       }
   }
}
