var AppDispatcher = require('./../../Common/dispatcher.js'),
    EventEmitter = require('events').EventEmitter,
    StateConstants = require('./../actions/StateConstants.js'),
    assign = require('object-assign'),
    Errors = require('./../../Errors.js'),
    ServerComm = require('./../../Common/ServerCommunication.js');

var STATE_CHANGE = 'state_change';

var ServerRequest = assign({}, EventEmitter.prototype, {

    _requestType: undefined,

    _requestMeta: undefined,

    _responseData: undefined,

    _countryStates: {},

    getRequestType: function() {
        return this._requestType;
    },

    getRequestMeta: function() {
        return this._requestMeta;
    },

    getCountryDerivedInformation: function(countryName) {
        if (!this._countryStates[countryName]) {
            return {
                option: false
            }
        }
        return this._countryStates[countryName];
    },

    setRequest: function(type, meta) {
        var self = this;
        this._requestType = type;
        this._requestMeta = meta;
        this._countryStates = {};
        if (meta.possibles && type == "territory") {
            this._countryStates = {};
            meta.possibles.forEach(function(country) {
                self._countryStates[country.id] = {
                    option: true
                };
            });
        }
        this.emitChange("request");
    },

    setResponseData: function(data) {
        this._responseData = data;
        this.emitChange("response");
    },

    popResponseData: function() {
        var a = this._responseData;
        this.emitChange("clear");
        return a;
    },

    emitChange: function(type) {
        this.emit(type);
    },

    addChangeListener: function(type, callback) {
        this.on(type, callback);
    },

    removeChangeListener: function(type) {
        this.removeListener(type, callback);
    }
});

ServerRequest.setMaxListeners(50);

// Register to handle all updates
AppDispatcher.register(function(payload) {
    var action = payload.action;

    if (payload.source === "SERVER_ACTION") {
        if (payload.action.requestType == "card") {
            console.log(payload.action);
        }
        ServerRequest.setRequest(payload.action.requestType, payload.action);
    }

    else if (payload.source == "PLAYER_ACTION") {
        ServerRequest.setResponseData(payload.action.response);
    }

    return true; // No errors.  Needed by promise in Dispatcher.
});


module.exports = ServerRequest;