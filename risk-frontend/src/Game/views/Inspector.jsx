var React = require('react'),
    StateStore = require('../stores/StateStore'),
    ViewStore = require('../stores/ViewStore');

module.exports = React.createClass({

    getInitialState: function() {
        return {
            gameState: StateStore.getCurrentState(),
            viewState: ViewStore.getViewState()
        }
    },

    componentDidMount: function () {
        ViewStore.addChangeListener("view_change", this._viewStoreChange);
    },

    _viewStoreChange: function() {
        this.setState({ viewState: ViewStore.getViewState() });
    },

    render: function() {
        return <div>
                <h1>Inspector</h1>
                { this.state.viewState.mostRecentId }
                <div className=""></div>
            </div>
    }

});