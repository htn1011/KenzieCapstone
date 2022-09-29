import BaseClass from "../util/baseClass";
import axios from 'axios'

// @TODO: this client will call the controller methods by accessing the endpoints defined in controller


export default class summaryClient extends BaseClass {

    constructor(props = {}){
        super();
        // @TODO the client must bind all methods that will be used
        const methodsToBind = ['clientLoaded', 'postNewSummary', 'findGameSummaryFromUser', 'updateGameSummary',
            'deleteSummaryBySummaryId', 'findAllSummariesForDate', 'findAllSummariesForUser', 'addNewUser', 'findUser',
            'findAllSummariesForUserFriends', 'addFriend', 'removeFriend'];
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

    // @TODO SUMMARY SIDE controller methods

    // date does not get input here, is generated in backend - KK
    async postNewSummary(game, userId, sessionNumber, results, errorCallback=console.error) {
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
            const response = await this.client.get(`/game/wordle/${summaryDate}/${userId}`);
            return response.data;
        } catch (error) {
            this.handleError("findGameSummaryFromUser", error, errorCallback)
        }
    }

    // updateGameSummary
    async updateGameSummary(existingSummaryDate, userId, game, updatedResults, errorCallback=console.error) {
        try {
            const response = await this.client.put(`/game/wordle/editSummary`, {
                "existingSummaryDate": existingSummaryDate,
                "userId": userId,
                "game": game,
                "updatedResults": updatedResults
            });
            return response.data;
        } catch (error) {
            this.handleError("updateGameSummary", error, errorCallback);
        }
    }

    // deleteSummaryBySummaryId
    async deleteSummaryBySummaryId(summaryDate, userId, errorCallback=console.error) {
        try {
            const response = await this.client.delete(`/game/wordle/${summaryDate}/${userId}`);
            return response.data;
        } catch (error) {
            this.handleError("deleteSummaryBySummaryId", error, errorCallback)
        }
    }

    // findAllSummariesForDate
    async findAllSummariesForDate(summaryDate, errorCallback) {
        try {
            const response = await this.client.get(`/game/wordle/${summaryDate}/all`);
            return response.data;
        } catch (error) {
            this.handleError("findAllSummariesForDate", error, errorCallback)
        }
    }

    // findAllSummariesForUser
    async findAllSummariesForUser(userId, errorCallback) {
        try {
            const response = await this.client.get(`/game/wordle/user/${userId}/all`);
            return response.data;
        } catch (error) {
            this.handleError("findAllSummariesForUser", error, errorCallback)
        }
    }

    // @TODO USER SIDE controller methods

    // addNewUser
    async addNewUser(userId, username, errorCallback=console.error) {
        try {
            const response = await this.client.post(`/game/wordle/user`, {
                "userId": userId,
                "username": username
            });
            return response.data;
        } catch (error) {
            this.handleError("addNewUser", error, errorCallback);
        }
    }

    // findUser
    async findUser(userId, errorCallback) {
        try {
            const response = await this.client.get(`/game/wordle/user/${userId}`);
            return response.data;
        } catch (error) {
            this.handleError("findUser", error, errorCallback)
        }
    }

    // findAllSummariesForUserFriends
    async findAllSummariesForUserFriends(summaryDate, userId, errorCallback) {
            try {
                const response = await this.client.get(`/game/wordle/{summaryDate}/{userId}/friends`);
                return response.data;
            } catch (error) {
                this.handleError("findAllSummariesForUserFriends", error, errorCallback)
            }
        }


    // addFriend
    async addFriend(userId, friendId, errorCallback=console.error) {
        try {
            const response = await this.client.put(`/game/wordle/user/${userId}/friends/add/${friendId}`);
            return response.data;
        } catch (error) {
            this.handleError("addfriend", error, errorCallback);
        }
    }

    // removeFriend
    async removeFriend(userId, friendId, errorCallback=console.error) {
        try {
            const response = await this.client.put(`/game/wordle/user/${userId}/friends/remove/${friendId}`);
            return response.data;
        } catch (error) {
            this.handleError("removeFriend", error, errorCallback);
        }
    }



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
           errorCallback(method + " failed - " + error, error);
       }
   }
}
