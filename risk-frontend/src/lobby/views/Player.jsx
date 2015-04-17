var Require = require('react'),
    FluxMixin = Fluxxor.FluxMixin(React),
    StoreWatchMixin = Fluxxor.StoreWatchMixin,
    GameView = require('./GameView.jsx');

var Application = React.createClass({


    render: function() {
        var playerState = this.props.playerState;

        return (
            <div>
                <div>{ playerState.name }</div>
                <div>{ playerState.type }</div>
                <div>{ playerState.connected }</div>
            </div>
        );
    }

});
