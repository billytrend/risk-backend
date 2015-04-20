var React = require('react/addons'),
    Store = require('../Store'),
    Actions = require('../Actions'),
    Player = require('./Player.jsx');
;

module.exports = React.createClass({


    _selectGame: function() {
        Actions.joinGame(this.props.index);
    },

    render: function() {
        var gameState = this.props.game;

        //if (gameState === undefined) {
        //    return (
        //        <div>
        //            <input value={ gameState.gameName ? gameState.gameName : '' } />
        //            {
        //                gameState.players.map(function() {
        //                    return <Player />;
        //                })
        //            }
        //        </div>
        //    );
        //}

        var self = this;

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
