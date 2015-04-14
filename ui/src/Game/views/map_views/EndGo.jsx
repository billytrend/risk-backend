var React = require('react/addons'),
    Die = require('./Die.jsx'),
    ServerRequest = require('../../stores/ServerRequest'),
    ServerActions = require('../../actions/ServerActions');

module.exports = React.createClass({

    submit: function() {
          ServerActions.selectTerritory(null);
    },

    render: function() {
        var self = this;

        return <button className="end_go" onClick={ self.submit }>End Go</button>
    }
});
