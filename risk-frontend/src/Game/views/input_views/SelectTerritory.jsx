var React = require('react'),
    ServerActions = require('../../actions/ServerActions'),
    ViewActions = require('../../actions/ViewActions'),
    ViewStore = require('../../stores/ViewStore'),
    ServerRequest = require('../../stores/ServerRequest');

module.exports = React.createClass({

    getInitialState: function() {
        return {serverMeta: ServerRequest.getRequestMeta()}
    },

    componentDidMount: function() {
        ViewStore.addChangeListener("view_change", this._selectTerritoryFromMap);
    },

    componentWillUnmount: function() {
        ViewStore.removeChangeListener("view_change", this._selectTerritoryFromMap);
    },

    _selectTerritoryFromMap: function () {
        this.setState({
            chosen: ViewStore.getCurCountry()
        });
    },

    _selectTerritory: function(event) {
        ViewActions.selectCountry(event.target.value);
    },

    render: function() {
        var self = this;
        var chosenIsAvailable = self.props.serverMeta.possibles.reduce(function(prev, cur) {return prev || cur.id === self.state.chosen}, false);
        return <div>
            <select value={ chosenIsAvailable ? this.state.chosen : "choose" } onChange= { this._selectTerritory }>
                {
                    self.props.serverMeta.possibles.map(function(territory) {
                        return <option value={ territory.id }>{ territory.id }</option>
                    }).concat(
                        <option value="choose">Please choose</option>
                    )
                }
            </select>
            <button onClick={ ServerActions.selectTerritory.bind(this, self.state.chosen) }>Submit</button>
        </div>
    }
});