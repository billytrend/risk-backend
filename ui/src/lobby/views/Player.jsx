var React = require('react'),
    Fluxxor = require('../Dispatcher');

module.exports = React.createClass({


    render: function() {
        var playerState = this.props.playerState;

        return (
            <div>
                <div>{ playerState.id }</div>
            </div>
        );
    }

});
