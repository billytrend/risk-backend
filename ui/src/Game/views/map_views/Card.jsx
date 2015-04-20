var React = require('react/addons'),
    MapRep = require('../../../Assets/map.json'),
    ViewStore = require('../../stores/ViewStore'),
    ViewActions = require('../../actions/ViewActions'),
    StateStore = require('../../stores/StateStore'),
    ServerRequest = require('../../stores/ServerRequest'),
    Country = require('./Country.jsx'),
    ServerActions = require('../../actions/ServerActions');

module.exports = React.createClass({

    componentDidMount: function() {
        pathBBox = this.refs.country.getDOMNode().children[0].getBBox();
        this.refs.country.getDOMNode().setAttribute("viewBox", "0 0 " + pathBBox.width + " " + pathBBox.height);
        this.refs.country.getDOMNode().setAttribute("width", pathBBox.width );
    },

    render: function() {
        var number = this.props.value ? this.props.value : 1,
            self = this;

        return (
            <div className="card">
                <div>
                    {
                        (function(type) {
                            switch (type) {
                                case 'SOLDIER': return "S";
                                case 'HORSE': return "H";
                                case 'CANNON': return "C";
                                case 'WILD': return "W";
                                default: break;
                            }
                        })(self.props.cardType)
                    }
                </div>
                <div>{ self.props.countryName }</div>
                <svg ref="country">
                    <Country countryName={self.props.countryName}/>
                </svg>
            </div>
        )
    }
});

//<Country countryName={ self.props.countryName } />
