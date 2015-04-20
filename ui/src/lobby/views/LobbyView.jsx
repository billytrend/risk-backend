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
                <h1 className="game_title">World Domination</h1>
                <QuickPlay />
                <h2>Waiting Games</h2>
                <div className="games">
                    <CreateGame />
                    {
                        this.state.unstartedGames.map(function(e, i) {
                            return <GameView game={ e } index={ i } />;
                        })
                        }
                </div>
                <h2>Games in progress</h2>
                <div className="games">
                    {
                        this.state.startedGames.map(function(e, i) {
                            return <GameView game={ e } index={ i } />;
                        })
                        }
                </div>
            </div>
        );
    }

});
