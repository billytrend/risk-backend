
var AppDispatcher = require('./../../Common/dispatcher.js');
var EventEmitter = require('events').EventEmitter;
var StateConstants = require('./../actions/StateConstants.js');
var assign = require('object-assign');

//Should be able to deal with these state changes
// {"source":{"id":"lol"},"target":{"id":"lola"},"armyOwner":{"id":"Player 250075633"},"amount":3,"actingPlayer":{"id":"Player 250075633"},"actionPlayed":"BEGINNING_STATE","changeType":"ArmyMovement"}
// {"target":{"id":"lol"},"armyOwner":{"id":"Player 358699161"},"amount":2,"actingPlayer":{"id":"Player 358699161"},"actionPlayed":"BEGINNING_STATE","changeType":"ArmyPlacement"}
// {"defendersLoss":0,"attackersLoss":0,"attacker":{"id":"Player 517938326"},"defender":{"id":"Player 914424520"},"attackingTerritory":{"id":"lol"},"defendingTerritory":{"id":"lola"},"actingPlayer":{"id":"Player 517938326"},"actionPlayed":"PLAYER_INVADING_COUNTRY","changeType":"FightResult"}
// {"removedPlayer":{"id":"Player 425918570"},"state":{"map":{},"players":{"Player 1":{"cards":[],"territories":[]},"Player 2":{"cards":[],"territories":[]}}},"actingPlayer":{"id":"Player 110718392"},"actionPlayed":"PLAYER_INVADING_COUNTRY","changeType":"PlayerRemoval"}

var initState = require('./../exampleState');

initState.map.countries.forEach(function(country) {
    initState.ownerships[country] = {
        armies: 0
    }
});


var StateStore = assign({}, EventEmitter.prototype, {

    _gameState: {
        "map": {},
        "ownerships" : {},
        "players": {},
        "currentGo": 0,
        "deck": [],
        "currentGo": ""
    },

    _changes: [],

    _gameStarted: false,

    _lastChange: {},

    getCountryState: function(country) {
        if (this._gameState.ownerships[country] == undefined) {
            return {
                armies: 0
            }
        }
        return this._gameState.ownerships[country];
    },

    getPlayerState: function(player) {
        var pl = {};
        if  (player in this._gameState.players) {
            pl = this._gameState.players[player];
            pl.isInPlay = this._gameState.currentGo == player;
        }
        return pl;
    },

    getCurrentState: function() {
        return this._gameState;
    },

    getStateStack: function() {
        return _stateStack;
    },

    applyArmyMovement: function(change) {

        // remove armies
        this._gameState.ownerships[change.sourceId].armies -= change.amount;
        // add armies
        this._gameState.ownerships[change.targetId].armies += change.amount;
        // notify parties
        this.emitChange('remove_army:' + change.sourceId);
        this.emitChange('add_army:' + change.targetId);
    },

    applyArmyPlacement: function(change) {

        // add armies
        this._gameState.ownerships[change.targetId].armies += change.amount;
        this._gameState.ownerships[change.targetId].owner = change.actingPlayerId;
        this._gameState.players[change.actingPlayerId].armies -= change.amount;

        // notify parties
        this.emitChange('add_army:' + change.targetId);
        this.emitChange('deployed_armies:' + change.actingPlayerId);
    },

    applyFightResult: function(change) {

        if (change.defendersLoss != 0) {
            this._gameState.ownerships[change.defendingTerritoryId].armies -= change.defendersLoss;
            this.emitChange('remove_army:' + change.defendingTerritoryId);
        }

        if (change.attackingTerritoryId != 0) {
            this._gameState.ownerships[change.attackingTerritoryId].armies -= change.attackersLoss;
            this.emitChange('remove_army:' + change.attackingTerritoryId);
        }

        if (this._gameState.ownerships[change.defendingTerritoryId].armies === 0) {
            this._gameState.ownerships[change.defendingTerritoryId].armies += change.attackDiceRolled.length;
            this._gameState.ownerships[change.attackingTerritoryId].armies -= change.attackDiceRolled.length;
            this._gameState.ownerships[change.defendingTerritoryId].owner = change.actingPlayerId;
            this.emitChange('add_army:' + change.defendingTerritoryId);
            this.emitChange('remove_army:' + change.attackingTerritoryId);
        }

    },

    applyPlayerRemoval: function(change) {
        this._gameState.players[change.actingPlayerId].isDead = true;
        this.emitChange("go_change")
    },

    applyCardHandout: function(change) {
        if (!this._gameState.players[change.actingPlayerId].cards) this._gameState.players[change.actingPlayerId].cards = [];
        this._gameState.players[change.actingPlayerId].cards.push(change.card);
    },

    applyArmyHandout: function(change) {
        if (!this._gameState.players[change.actingPlayerId].armies) {
            this._gameState.players[change.actingPlayerId].armies = 0;
        }
        this._gameState.players[change.actingPlayerId].armies += change.amount;
        this.emitChange('gained_armies:' + change.actingPlayerId);
    },

    applyPlayerChange: function(change) {
        this._gameState.currentGo = change.actingPlayerId;
        this.emitChange("go_change")
    },

    setChange: function(ch) {
        this._lastChange = ch;
    },

    getChange: function(ch) {
        return this._lastChange;
    },

    applyGameStart: function(change) {
        this._gameState = change.gameState;
        this._gameStarted = true;
        this.emit('state_change');
    },

    gameStarted: function() {
        return this._gameStarted;
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

    if (StateStore['apply' + payload.action.changeType]) {
        StateStore['apply' + payload.action.changeType](payload.action);
        StateStore.setChange(payload.action);
        StateStore.emitChange("change_happened");
    }

    return true; // No errors.  Needed by promise in Dispatcher.
});

module.exports = StateStore;