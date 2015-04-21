var React = require('react'),
    StateStore = require('../stores/StateStore'),
    MapRep = require("../../Assets/map.json"),
    Country = require('./map_views/Country.jsx'),
    Die = require('./map_views/Die.jsx'),
    DieBox = require('./map_views/DieBox.jsx'),
    Continents = require('./map_views/Continents.jsx'),
    Connections = require('./map_views/Connections.jsx');


module.exports = React.createClass({

    getInitialState: function() {
        return StateStore.getCurrentState();
    },

    render: function() {
        //console.log(Countries)
        return <div className="world_map">
            <svg width="100%" height="100%"  viewBox="0 0 1016 658" >
                <Connections />
                <g>
                    <defs/>
                    {
                        Object.keys(MapRep.countries).map(function(name) {
                            return <Country countryName={ name } onMap={ true }/>
                        })
                        }
                </g>
            </svg>
        </div>
    }

});