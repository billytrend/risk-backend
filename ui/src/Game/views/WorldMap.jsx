var React = require('react'),
    StateStore = require('../stores/StateStore'),
    MapRep = require("../../Assets/map.json"),
    Country = require('./map_views/Country.jsx'),
    Die = require('./map_views/Die.jsx'),
    DieBox = require('./map_views/DieBox.jsx');

var filter =
  "<linearGradient x1='50%' y1='0%' x2='50%' y2='100%' id='die-pip'>"
+ "    <stop stop-color='#7B7B7B' offset='0%'></stop>"
+ "    <stop stop-color='#282828' offset='100%'></stop>"
+ "</linearGradient>"
+ "<filter id='lol'>"
+ "    <feDiffuseLighting in='SourceGraphic' result='light' lighting-color='white'>"
+ "        <fePointLight x='500' y='500' z='300'></fePointLight>"
+ "    </feDiffuseLighting>"
+ "    <feComposite in='SourceGraphic' in2='light' operator='arithmetic' k1='1' k2='0' k3='0' k4='0'></feComposite>"
+ "</filter>";



module.exports = React.createClass({

    getInitialState: function() {
        return StateStore.getCurrentState();
    },

    render: function() {
        //console.log(Countries)
        return <div className="world_map">
            <svg width="100%" height="100%"  viewBox="0 0 1016 658" >
                <g>
                    <defs dangerouslySetInnerHTML={{ __html: filter }} />
                    {
                        Object.keys(MapRep.countries).map(function(name) {
                            return <Country countryName={ name } />
                        })
                    }
                </g>
            </svg>
        </div>
    }

});