var React = require('react/addons'),
    Die = require('./Die.jsx'),
    Country = require('./Country.jsx'),
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

    componentDidMount: function() {
        this._hugSvgs();
    },

    _hugSvgs: function() {
        var pathBBox = this.refs.svgA.getDOMNode().children[0].getBBox();
        this.refs.svgA.getDOMNode().setAttribute("viewBox", "0 0 " + pathBBox.width + " " + pathBBox.height);
        this.refs.svgA.getDOMNode().setAttribute("width", pathBBox.width );
        pathBBox = this.refs.svgB.getDOMNode().children[0].getBBox();
        this.refs.svgB.getDOMNode().setAttribute("viewBox", "0 0 " + pathBBox.width + " " + pathBBox.height);
        this.refs.svgB.getDOMNode().setAttribute("width", pathBBox.width );

    },

    render: function() {
        var self = this;
        return (
            <div className="view_layer">
                <div className="dialogue_box">
                    <svg ref="svgA">
                        <Country countryName={ self.state.meta.attacking.id } />
                    </svg>
                    <svg ref="svgB">
                        <Country countryName={ self.state.meta.defending.id } />
                    </svg>
                        { this.state.meta.max > 0 ? <Die index={ 1 } attackDice={ self.state.meta.reason === "ATTACK_CHOICE_DICE" } value={ parseInt(this.props.first) } /> : '' },
                        { this.state.meta.max > 1 ? <Die index={ 2 } attackDice={ self.state.meta.reason === "ATTACK_CHOICE_DICE" } value={ parseInt(this.props.second) } /> : '' },
                        { this.state.meta.max > 2 ? <Die index={ 3 } attackDice={ self.state.meta.reason === "ATTACK_CHOICE_DICE" } value={ parseInt(this.props.third) } /> : '' }
                </div>
            </div>
        )
    }
});