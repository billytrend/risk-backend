var React = require('react'),
    StateStore = require('../stores/StateStore'),
    ViewActions = require('../actions/ViewActions'),
    DieBox = require('./map_views/DieBox.jsx');

module.exports = React.createClass({

    render: function() {
        return <div>
            <h1>Map</h1>
            <div className="map-container" ref="mapContainer"></div>
        </div>
    }
});