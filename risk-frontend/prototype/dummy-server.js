var WebSocketServer = require('websocket').server;
var http = require('http');

var server = http.createServer(function(request, response) {
    console.log((new Date()) + ' Received request for ' + request.url);
    response.writeHead(404);
    response.end();
});

server.listen(8080, function() {
    console.log((new Date()) + ' Server is listening on port 8080');
});

wsServer = new WebSocketServer({
    httpServer: server,
    // You should not use autoAcceptConnections for production
    // applications, as it defeats all standard cross-origin protection
    // facilities built into the protocol and the browser.  You should
    // *always* verify the connection's origin and decide whether or not
    // to accept it.
    autoAcceptConnections: false
});

function originIsAllowed(origin) {
    // put logic here to detect whether the specified origin is allowed.
    return true;
}

wsServer.on('request', function(request) {

    var connection = request.accept();

    connection.send("{\"requestType\":\"number_of_armies\",\"max\":10}");
    //connection.send("{\"requestType\":\"territory\",\"possibles\":[{\"id\":\"northern_europe\"},{\"id\":\"test\"},{\"id\":\"test\"}],\"canResign\":false}");
    //connection.send("{\"requestType\":\"territory\",\"possibles\":[{\"id\":\"northern_europe\"},{\"id\":\"test\"},{\"id\":\"test\"}],\"canResign\":false}");
    //connection.send("{\"requestType\":\"number_of_dice\",\"max\":3}");
    connection.on('message', function(message) {
    });

    connection.on('close', function(reasonCode, description) {
        console.log((new Date()) + ' Peer ' + connection.remoteAddress + ' disconnected.');
    });
});
