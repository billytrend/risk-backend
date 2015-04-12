var React = require('react'),
    ServerActions = require('../../actions/ServerActions'),
    ServerRequest = require('../../stores/ServerRequest');

var generateArmies = function(state, click) {
    var dice = [];
    for(var i = 1; i <= state.max; i++) {
        dice.push(
            <button id={ i } onClick={ click.bind(null, i) }>{ i }</button>
        );
    }
    return dice;
};

module.exports = React.createClass({

    getInitialState: function() {
        return ServerRequest.getRequestMeta();
    },

    render: function() {
        var self = this;

        return <div className="dice">
            <div>
                {
                    generateArmies(this.state, ServerActions.selectArmies)
                }
            </div>
        </div>
    }
});