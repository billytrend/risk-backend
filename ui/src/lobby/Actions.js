

module.exports.constants = {
    CREATE_GAME: "ADD_TODO"
};


module.exports.actions = {
    createGame: function(game) {
        this.dispatch(constants.CREATE_GAME, game);
    }
};
