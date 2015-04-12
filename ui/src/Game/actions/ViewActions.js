var AppDispatcher = require('./../../Common/dispatcher.js');
var ViewChangeConstants = require('./ViewChangeConstants');

var TodoActions = {

    selectCountry: function(id) {
        AppDispatcher.handleViewAction({
            actionType: ViewChangeConstants.SELECT_COUNTRY_ON_MAP,
            countryId: id
        });
    },

    selectPlayer: function(id) {
        AppDispatcher.handleViewAction({
            actionType: ViewChangeConstants.SELECT_PLAYER,
            playerId: id
        });
    }

};

module.exports = TodoActions;