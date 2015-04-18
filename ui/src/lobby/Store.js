
var AppDispatcher = require('../Common/dispatcher.js'),
    EventEmitter = require('events').EventEmitter,
    assign = require('object-assign');

var StateStore = assign({}, EventEmitter.prototype, {

    _lobbyState: {
        unstartedGames: [],
        startedGames: []
    },

    _waitingIndex: undefined,

    _changes: [],

    getLobbyState: function() {
        return this._lobbyState;
    },

    setGameToBeJoined: function(index) {
        this._waitingIndex = index;
        this.emitChange("waiting_game");
    },

    getGameToBeJoined: function() {
        return this._lobbyState.unstartedGames[this._waitingIndex];
    },

    setLobbyState: function(state) {
        this._lobbyState = state;
        this.emitChange("lobby_update");
    },

    emitChange: function(type) {
        this.emit(type);
    },

    addChangeListener: function(change, callback) {
        this.on(change, callback);
    },

    removeChangeListener: function(change, callback) {
        this.removeListener(change, callback);
    }

});

StateStore.setMaxListeners(50);

// Register to handle all updates
AppDispatcher.register(function(payload) {
    var action = payload.action;

    StateStore.emitChange();

    if (payload.action.lobbyUpdate) {
        StateStore.setLobbyState(payload.action);
    }

    if (payload.action.commandType == "JoinGameReq") {
        StateStore.setGameToBeJoined(payload.action.index);
    }
    if (payload.action.changeType) {
        StateStore.emit("game_start");
    }



    return true;
});

module.exports = StateStore;