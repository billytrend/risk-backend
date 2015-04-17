var Require = require('react'),
    FluxMixin = Fluxxor.FluxMixin(React),
    StoreWatchMixin = Fluxxor.StoreWatchMixin,
    GameView = require('./GameView.jsx');

var Application = React.createClass({

    mixins: [FluxMixin, StoreWatchMixin("LobbyStore")],

    getInitialState: function() {
        var flux = this.getFlux();
        return { newTodoText: "" };
    },

    getStateFromFlux: function() {
        var flux = this.getFlux();

        return flux.store("LobbyStore").getState();
    },

    render: function() {
        return (
            <div>
                {
                    this.state.games.map(function(e) {
                        return <GameView game={ e }/>;
                    })
                }
            </div>
        );
    }

});
