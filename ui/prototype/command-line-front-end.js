var io = require('ws'),
    conn = new io('ws://localhost:8887'),
    emit = console.log;

var readline = require('readline');

var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

var selectDice = function(req) {
    emit("Please select a number of dice.");
    emit("Max : " + req.max);
    emit("Because : " + req.reason);


    rl.on('line', function(line) {
            conn.send(JSON.stringify({
                commandType: "DiceNumberResponse",
                n: line
            }));
        });
};

var selectArmy = function(req) {
    emit("Please select a number of armies.");
    emit("Max : " + req.max);
    emit("Because : " + req.reason);

    rl.on('line', function(line) {
            conn.send(JSON.stringify({
                commandType: "ArmyResponse",
                n: line
            }));
        });
};

var selectTerritory = function(req) {
    emit("Please input a territory.");
    emit("Becase " + req.reason);
    req.possibles.forEach(function(terr, i) {
        emit(i + ". " + terr.id);
    });

    rl.on('line', function(line) {
            conn.send(JSON.stringify({
                commandType: "TerritoryResponse",
                territory: req.possibles[line].id
            }));
        });
};

conn.on('message', function(message) {
    var messageObj = JSON.parse(message);

    rl.removeAllListeners();

    var options = {
        'number_of_dice': selectDice,
        'territory': selectTerritory,
        'number_of_armies': selectArmy,
        'card_selection': undefined
    };

    options[messageObj.requestType](messageObj);

});

conn.on('error', function() {});