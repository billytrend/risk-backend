var conn = new WebSocket('ws://138.251.207.158:8887'),
    Dispatcher = require('./dispatcher'),
    ServerRequest = require('../Game/stores/ServerRequest');

conn.onmessage = function(message) {
    var messageObj = JSON.parse(message.data);
    console.log(messageObj);
    Dispatcher.handleServerAction(messageObj);
};

var sendMessage = function(response) {
    if (!response) response = ServerRequest.popResponseData();
    conn.send(JSON.stringify(response));
};

ServerRequest.addChangeListener("response", sendMessage);

module.exports.sendMessage = sendMessage;