var React = require('react'),
    ServerRequest = require('../stores/ServerRequest');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            message: "Waiting...",
            show: true
        }
    },

    componentDidMount: function() {
        ServerRequest.addChangeListener("request", this._updateMessage);
    },

    _updateMessage: function() {
        var req = ServerRequest.getRequestType();
        var meta = ServerRequest.getRequestMeta();
        if (meta.humanRequest) {
            this.setState({
                message: meta.humanRequest,
                show: true
            });
        } else {
            this.setState({
                show: false
            });
        }
    },

    render: function() {
        var cx = React.addons.classSet;

        return <div className={ cx({prompt:true, prompt_hidden: !this.state.show }) }>
            {
                this.state.message
            }
        </div>
    }
});