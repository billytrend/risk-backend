var React = require('react/addons'),
    Die = require('./Die.jsx'),
    ServerRequest = require('../../stores/ServerRequest'),
    ServerActions = require('../../actions/ServerActions');

var filters = {
    selected: 'selected',
    option: 'option'
};

module.exports = React.createClass({

    getInitialState: function() {
        return {
            request: ServerRequest.getRequestType(),
            meta: ServerRequest.getRequestMeta()
        };
    },

    render: function() {

        return (
            <div className="view_layer dice_box">
                <div>
                    { this.state.meta.max > 0 ? <Die index={ 1 } value={ parseInt(this.props.first) } /> : '' },
                    { this.state.meta.max > 1 ? <Die index={ 2 } value={ parseInt(this.props.second) } /> : '' },
                    { this.state.meta.max > 2 ? <Die index={ 3 } value={ parseInt(this.props.third) } /> : '' }
                </div>
            </div>
        )
    }
});
