var React = require('react'),
    Actions = require('../../actions/Actions.js'),
    ServerRequest = require('../../stores/ServerRequest');

module.exports = React.createClass({

    getInitialState: function() {
        return ServerRequest.getRequestMeta();
    },

    _selectDice: function(no) {
        Actions.respondToRequest({
            response: no
        });
    },

    render: function() {
        var self = this;

        return <div className="dice">
            <div>
                {
                    generateDice(this.state, this._selectDice)
                }
            </div>
        </div>
    }
});