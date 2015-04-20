var React = require('react'),
    StateStore = require('../stores/StateStore')
module.exports = React.createClass({

    getInitialState: function() {
        var player = StateStore.getPlayerState(this.props.player);
        player.style = {
            backgroundColor: '#' + player.colour
        };
        return player;
    },

    componentDidMount: function() {
        StateStore.addChangeListener("go_change", this._refreshState);
        StateStore.addChangeListener("gained_armies:" + this.state.id, this._refreshState);
        StateStore.addChangeListener("deployed_armies:" + this.state.id, this._refreshState);
    },

    _refreshState: function() {
        var player = StateStore.getPlayerState(this.props.player);
        this.setState(player);
    },

    render: function() {
        var player = this.state;
        var cx = React.addons.classSet;
        return <div style={ this.state.style } className={ cx({player_icon: true, in_play: player.isInPlay, is_dead: player.isDead }) } >
            <div>{ player.id }</div>
            <div>{ player.armies }</div>
        </div>
    }
});