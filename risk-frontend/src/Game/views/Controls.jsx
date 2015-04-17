var React = require('react'),
    SelectDice = require('./input_views/SelectDice.jsx'),
    SelectTerritory = require('./input_views/SelectTerritory.jsx'),
    SelectNumberOfArmies = require('./input_views/SelectNumberOfArmies.jsx'),
    StateTypes = require('../actions/StateConstants'),
    ServerRequest = require('../stores/ServerRequest');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            stateType: ServerRequest.getRequestType()
        }
    },

    componentDidMount: function () {
        ServerRequest.addChangeListener("request", this._serverRequest);
    },

    _serverRequest: function() {
        this.setState({
            serverRequest: ServerRequest.getRequestType(),
            requestMeta: ServerRequest.getRequestMeta()
        });
    },

    render: function() {
        var self = this;
        return <div>
            <h1>Controls</h1>
            {
                (function() {
                    switch (self.state.serverRequest) {

                        case 'number_of_dice':
                            return <SelectDice />;

                        case 'territory':
                            return <SelectTerritory serverMeta={ ServerRequest.getRequestMeta() }/>;

                        case 'number_of_armies':
                            return <SelectNumberOfArmies />;

                        case 'card_selection':
                            return <SelectNumberOfArmies />;

                        default:
                            return <h2>No control</h2>;
                    }
                })()
            }
        </div>
    }
});