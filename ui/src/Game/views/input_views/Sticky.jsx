var React = require('react'),
    Vis = require('vis'),
    Actions = require('../../../actions/Actions.js');


module.exports = React.createClass({

    getInitialState: function() {
        return { mode: '' }
    },

    render: function() {
        var self = this;
        return <div>
            {
                (function() {
                    switch (self.state.mode) {
                        default: return (
                            <div>
                                <h1>Controls</h1>
                                <button></button>
                                <button></button>
                                <button></button>
                                <button></button>
                                <button></button>
                            </div>
                        );
                    }
                })()
            }
        </div>
    }
});