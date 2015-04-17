var React = require('react'),
    StateStore = require('../stores/StateStore')
module.exports = React.createClass({

    getInitialState: function() {
        var player = StateStore.getPlayerState(this.props.player);
        player.style = {
            backgroundColor: '#' + player.colour,
            borderStyle: player.inPlay ? 'solid' : 'none'
        };
        return player;
    },

    componentDidMount: function() {
        StateStore.addChangeListener("go_change", this._refreshState);
    },

    _refreshState: function() {
        var player = StateStore.getPlayerState(this.props.player);
        player.style = {
            backgroundColor: '#' + player.colour,
            borderStyle: player.isInPlay ? 'solid' : 'none'
        };
        this.setState(player);
    },

    render: function() {
        var player = this.state;
        return <div style={ this.state.style } className="player_icon">
            <div>{ player.id }</div>
            <div>{ player.undeployedArmies }</div>
            <div>{ player.totalArmies }</div>
        </div>
    }
});