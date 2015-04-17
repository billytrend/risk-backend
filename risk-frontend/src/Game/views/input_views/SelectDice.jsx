var React = require('react'),
    ServerActions = require('../../actions/ServerActions'),
    ServerRequest = require('../../stores/ServerRequest');

var generateDice = function(state, click) {
    var dice = [];
    for(var i = 1; i <= state.max; i++) {
        dice.push(
            <button id={ i } onClick={ click.bind(undefined, i) }>{ i }</button>
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
        console.log(this.state);

        return <div className="dice">
            <div>
                {
                    generateDice(self.state, ServerActions._selectDice)
                }
            </div>
        </div>
    }
});