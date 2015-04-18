var React = require('react/addons'),
    Store = require('../Store'),
    Actions = require('../Actions'),
    Player = require('./Player.jsx');

module.exports = React.createClass({

    getInitialState: function () {
        return Store.getGameToBeJoined();
    },

    componentDidMount: function() {
        Store.addChangeListener("waiting_game", this._refreshState);
    },

    componentWillUnmount: function() {
        Store.removeChangeListener("waiting_game", this._refreshState);
    },

    _selectGame: function() {
        Actions.joinGame(this.props.index);
    },

    _refreshState: function() {
        this.setState(Store.getGameToBeJoined());
    },

    render: function() {

        var gameState = this.state;

        if (!gameState) return <div></div>;

        return (
            <div className="game">
                <div className="gameId">{ gameState.id } ({gameState.players.length}/{gameState.maxPlayers})</div>
                <div>
                    {
                        gameState.players.map(function(player) {
                            return <Player playerState={ player }/>;
                        })
                        }
                </div>
                <div>
                    <button onClick={ self._selectGame } >Join game</button>
                    <button>Watch game</button>
                </div>
            </div>
        );
    }

});
