var React = require('react'),
    ServerRequest = require('../stores/ServerRequest');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            message: "Waiting..."
        }
    },

    componentDidMount: function() {
        ServerRequest.addChangeListener("request", this._updateMessage);
    },

    _updateMessage: function() {
        var req = ServerRequest.getRequestType();
        var meta = ServerRequest.getRequestMeta();
        this.setState({
            message: "Request of type " + req + " because " + meta.reason
        });
    },

    render: function() {
        return <div className="prompt">
        {this.state.message}
        </div>
    }
});