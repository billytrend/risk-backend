var conn = new WebSocket('ws://risk.billytrend.com:8887'),
    Dispatcher = require('./dispatcher'),
    ServerRequest = require('../Game/stores/ServerRequest');

conn.onmessage = function(message) {
    var messageObj = JSON.parse(message.data);
    Dispatcher.handleServerAction(messageObj);
};

var sendMessage = function(response) {
    if (!response) response = ServerRequest.popResponseData();
    conn.send(JSON.stringify(response));
};

ServerRequest.addChangeListener("response", sendMessage);

module.exports.sendMessage = sendMessage;