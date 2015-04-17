var AppDispatcher = require('./../../Common/dispatcher.js');
var EventEmitter = require('events').EventEmitter;
var ViewChangeConstants = require('./../actions/ViewChangeConstants.js');
var assign = require('object-assign');
var Errors = require('./../../Errors.js');
var ServerComm = require('./../../Common/ServerCommunication.js');

var VIEW_CHANGE = 'view_change'

var _viewState = {};

var ViewStore = assign({}, EventEmitter.prototype, {

    getViewState: function() {
        return _viewState;
    },

    setCurCountry: function(id) {
        if ( _viewState.mostRecentId == id ) {
            this.setMostRecentSelection(undefined, undefined);
        } else {
            this.setMostRecentSelection('country', id);
        }
    },

    getCurCountry: function() {
        if (_viewState.mostRecentType == 'country') {
            return _viewState.mostRecentId;
        }
    },

    setCurPlayer: function(id) {
        _viewState.curPlayer = id;
        this.setMostRecentSelection('player', id);
    },

    setMostRecentSelection: function(type, id) {
        var prev = _viewState.mostRecentId;
        _viewState.mostRecentType = type;
        _viewState.mostRecentId = id;
        this.emitChange(id);
        this.emitChange(prev);
    },

    emitChange: function(change) {
        this.emit("view_change");
        if (change) {
            this.emit(change);
            console.log("alerting " + change)
        }
    },

    addChangeListener: function(change, callback) {
        this.on(change, callback);
    },

    removeChangeListener: function(change, callback) {
        this.removeListener(change, callback);
    }

});

// Register to handle all updates
AppDispatcher.register(function(payload) {
    var action = payload.action;

    if (payload.source === 'VIEW_ACTION') {
        handleViewAction(action);
    }

    else {
        return;
    }

    // This often goes in each case that should trigger a UI change. This store
    // needs to trigger a UI change after every view action, so we can make the
    // code less repetitive by putting it here.  We need the default case,
    // however, to make sure this only gets called after one of the cases above.
    ViewStore.emitChange();

    return true; // No errors.  Needed by promise in Dispatcher.
});

var handleViewAction = function(action) {

    switch (action.actionType) {
        case ViewChangeConstants.SELECT_COUNTRY_ON_MAP:
            ViewStore.setCurCountry(action.countryId);
            break;

        case ViewChangeConstants.SELECT_PLAYER:
            ViewStore.setCurPlayer(action.playerId);
            break;

        default:
            Errors.error("Unrecognised view action.");
    }

};


module.exports = ViewStore;
