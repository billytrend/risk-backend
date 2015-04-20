var React = require('react/addons'),
    Store = require('../Store'),
    Actions = require('../Actions'),
    Player = require('./Player.jsx');

module.exports = React.createClass({


    _selectGame: function() {
        Actions.joinGame(this.props.index);
    },

    render: function() {
        var gameState = this.props.game;
        var self = this;

        return (
            <div className="quick_play">
                <h2>Quick play</h2>
                <button onClick={ Actions.createGame.bind(null, "Quick-play", 1, ["Berserker","BorderControl","CommunistAggressive","CommunistDefensive","DumbBotInterface"]) }>Single player hard</button>
                <button onClick={ Actions.createGame.bind(null, "Quick-play", 1, ["Berserker","BorderControl","CommunistAggressive","CommunistDefensive","DumbBotInterface"]) }>Single player medium</button>
                <button onClick={ Actions.createGame.bind(null, "Quick-play", 1, ["Berserker","BorderControl","CommunistAggressive","CommunistDefensive","DumbBotInterface"]) }>Single player easy</button>
                <button onClick={ Actions.createGame.bind(null, "Quick-play", 0, ["Berserker","BorderControl","CommunistAggressive","CommunistDefensive","DumbBotInterface"]) }>Watch AI battle</button>
            </div>
        );
    }

});

