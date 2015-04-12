
var conn = new WebSocket('ws://localhost:8887'),
    Dispatcher = require('./dispatcher'),
    ServerRequest = require('../Game/stores/ServerRequest');

conn.onmessage = function(message) {
    Dispatcher.handleServerAction(JSON.parse(message.data));
};

var sendMessage = function() {
    var response = ServerRequest.popResponseData();
    conn.send(JSON.stringify(response));
};

ServerRequest.addChangeListener("response", sendMessage);
