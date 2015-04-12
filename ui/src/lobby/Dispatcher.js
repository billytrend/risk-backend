var LobbyStore = require('./Store'),
    LobbyActions = require('./Actions').actions;

var stores = {
    TodoStore: new LobbyActions()
};


var flux = new Fluxxor.Flux(stores, LobbyActions);



module.exports = flux;