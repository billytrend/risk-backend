var React = require('react'),
    Dispatcher = require('../Dispatcher'),
    Store = require('../Store'),
    GameView = require('./GameView.jsx'),
    WaitingView = require('./GameView.jsx');

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
            <div>
                <div>

                </div>
                <div className="games">
                    {
                        this.state.unstartedGames.map(function(e, i) {
                            return <GameView game={ e } index={ i } />;
                        })
                    }
                </div>
            </div>
        );
    }

});
