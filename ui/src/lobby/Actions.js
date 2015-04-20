var ServerComm = require('../Common/ServerCommunication');

var TodoActions = {

    joinGame: function(i) {
        ServerComm.sendMessage({
            commandType: 'JoinGameReq',
            gameIndex: i
        });
    },

    createGame: function(name, maxPlayers, ais) {
        ServerComm.sendMessage({
            commandType: 'GameDescription',
            id: name,
            maxPlayers: maxPlayers,
            AIs: ais,
            players: []
        });
    }

};

module.exports = TodoActions;