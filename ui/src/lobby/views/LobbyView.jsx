var React = require('react'),
    Dispatcher = require('../Dispatcher'),
    Store = require('../Store'),
    GameView = require('./GameView.jsx'),
    WaitingView = require('./GameView.jsx'),
    QuickPlay = require('./QuickPlay.jsx'),
    CreateGame = require('./CreateGame.jsx');

module.exports = React.createClass({


    getInitialState: function() {
        return Store.getLobbyState();
    },

    componentDidMount: function() {
        Store.addChangeListener("lobby_update", this._refreshState);
    },

    componentWillUnmount: function() {
        Store.removeChangeListener("lobby_update", this._refreshState);
    },

    _refreshState: function() {
        this.setState(Store.getLobbyState());
    },

    render: function() {
        return (
            <div className="lobby_view">
                <div className="main_lobby">
                    <h1 className="game_title">World Domination</h1>
                    <p>Games currently in play: { this.state.startedGames.length }</p>
                    <p>Games waiting for players: { this.state.unstartedGames.length }</p>
                    <QuickPlay />
                    <h2>Create Game</h2>
                    <CreateGame />
                    <h2>Waiting Games</h2>
                    <div className="games">
                        {
                            this.state.unstartedGames.map(function(e, i) {
                                return <GameView game={ e } index={ i } />;
                            })
                            }
                    </div>
                </div>
            </div>
        );
    }

});
