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
            meta: ServerRequest.getRequestMeta(),
            curHover: undefined
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

    _hover: function(i) {
        this.setState({
            curHover: i
        });
    },

    render: function() {
        var self = this;
        var cx = React.addons.classSet;

        return (
            <div className="view_layer">
                <div className="dialogue_box">
                    <div>
                        <svg ref="svgA">
                            <Country countryName={ self.state.meta.attacking.id } />
                        </svg>
                        <div>Attacking: { self.state.meta.attacking.id }</div>
                    </div>
                    <div>
                        <svg ref="svgB">
                            <Country countryName={ self.state.meta.defending.id } />
                        </svg>
                        <div>Defending: { self.state.meta.defending.id }</div>
                    </div>
                        { this.state.meta.max > 0 ? <Die index={ 1 } className={cx({die_hover:self.state.curHover > 0})} hoverFn={ self._hover } attackDice={ self.state.meta.reason === "ATTACK_CHOICE_DICE" } value={ parseInt(this.props.first) } /> : '' }
                        { this.state.meta.max > 1 ? <Die index={ 2 } className={cx({die_hover:self.state.curHover > 1})} hoverFn={ self._hover } attackDice={ self.state.meta.reason === "ATTACK_CHOICE_DICE" } value={ parseInt(this.props.second) } /> : '' }
                        { this.state.meta.max > 2 ? <Die index={ 3 } className={cx({die_hover:self.state.curHover > 2})} hoverFn={ self._hover } attackDice={ self.state.meta.reason === "ATTACK_CHOICE_DICE" } value={ parseInt(this.props.third) } /> : '' }
                </div>
            </div>
        )
    }
});