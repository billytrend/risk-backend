var Require = require('react'),
    FluxMixin = Fluxxor.FluxMixin(React),
    StoreWatchMixin = Fluxxor.StoreWatchMixin,
    GameView = require('./GameView.jsx');

var Application = React.createClass({

    render: function() {

        if (gameState === undefined) {
            var state = this.state;
            return (
                <div>
                    <input value={ state.gameName ? state.gameName : '' } />
                    {
                        gameState.players.map(function() {
                            return <Player />;
                        })
                    }
                </div>
            );
        }

        var gameState = this.props.gameState;

        return (
            <div>
                <div>{ gameState.name }</div>
                {
                    gameState.players.map(function(player) {
                        return <Player playerState={ player }/>;
                    })
                }
            </div>
        );
    }

});
