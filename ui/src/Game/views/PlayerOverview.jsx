var React = require('react'),
    PlayerIcon = require('./PlayerIcon.jsx'),
    StateStore = require('../stores/StateStore'),
    ViewStore = require('../stores/ViewStore');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            gameState: StateStore.getCurrentState(),
            viewState: ViewStore.getViewState()
        }
    },

    componentDidMount: function () {
        StateStore.addChangeListener("state_change", this._refreshState);
    },

    _viewStoreChange: function() {
        this.setState({ viewState: ViewStore.getViewState() });
    },

    _refreshState: function() {
        this.setState({
            gameState: StateStore.getCurrentState(),
            viewState: ViewStore.getViewState()
        });
    },

    render: function() {
        var self = this;
        return <div className="player_overview">
            <h2>Players</h2>
            {
                Object.keys(self.state.gameState.players).map(function(player, i) {
                    return <PlayerIcon key={ i } player={ player } />
                })
            }
        </div>
    }
});