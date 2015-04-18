var AppDispatcher = require('./../../Common/dispatcher.js');
var ActionConstants = require('./ActionConstants');

var TodoActions = {

    respondToRequest: function(response) {
        AppDispatcher.handlePlayerAction({
            actionType: 'request_response',
            response: response
        });
    }

};

module.exports = TodoActions;