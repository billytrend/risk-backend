var ServerComm = require('../Common/ServerCommunication');

var TodoActions = {

    joinGame: function(i) {
        ServerComm.sendMessage({
            commandType: 'JoinGameReq',
            gameIndex: i
        });
    }

};

module.exports = TodoActions;