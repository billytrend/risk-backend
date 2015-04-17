var React = require('react/addons'),
    MapRep = require('../../../Assets/map.json'),
    ViewStore = require('../../stores/ViewStore'),
    ViewActions = require('../../actions/ViewActions'),
    StateStore = require('../../stores/StateStore'),
    ServerRequest = require('../../stores/ServerRequest'),
    ServerActions = require('../../actions/ServerActions'),
    VelocityJS = require('velocity-animate'),
    tweenState = require('react-tween-state');

var filters = {
    selected: 'selected',
    option: 'option'
};

module.exports = React.createClass({
        mixins: [tweenState.Mixin],

        countryName: undefined,

        getInitialState: function() {
            var obj  = MapRep.countries[this.props.countryName];
            obj.style = MapRep.countryStyle;
            obj.labelStyle = MapRep.labelStyle;
            obj.labelX = 0;
            obj.labelY = 0;
            obj.labelOpacity = 1;
            return obj;
        },

        componentDidMount: function() {
            //ViewStore.addChangeListener(this.props.countryName, this._refreshState);
            StateStore.addChangeListener("state_change", this._refreshState);
            StateStore.addChangeListener("add_army:" + this.props.countryName, this._addArmy);
            StateStore.addChangeListener("remove_army:" + this.props.countryName, this._removeArmy);
            ServerRequest.addChangeListener("request", this._setOptionality);
            this.setLabelPos();
            this._refreshState();
        },

        componentWillUnmount: function() {
            //ViewStore.removeChangeListener(this.props.countryName, this._refreshState);
            StateStore.removeChangeListener("state_change", this._refreshState);
            StateStore.removeChangeListener("add_army:" + this.props.countryName, this._addArmy);
            StateStore.removeChangeListener("remove_army:" + this.props.countryName, this._removeArmy);
            ServerRequest.removeChangeListener("request", this._refreshState);
        },

        setLabelPos: function() {
            if (this.getDOMNode().getBBox) {
                var bBox = this.getDOMNode().getBBox();

                this.setState({
                    labelX: bBox.width / 2,
                    labelY: bBox.height/2
                });
            }
        },

        shouldComponentUpdate: function() {
            return true;
        },

        _refreshState: function() {

            var countryState = StateStore.getCountryState(this.props.countryName),
                countryRequestState = ServerRequest.getCountryDerivedInformation(this.props.countryName);

            var newState = {
                armies: countryState.armies
            };

            this.setState(newState);

            if (countryState.owner) {
                this._setFill('#' + StateStore.getPlayerState(countryState.owner).colour);
            }
        },

        _setOptionality: function() {

            var countryRequestState = ServerRequest.getCountryDerivedInformation(this.props.countryName),
                targetOpacity,
                lowOp = 0.2,
                normalOp = 1;

            if(countryRequestState.option ) {
                targetOpacity = normalOp;
            }

            else if(!countryRequestState.option) {
                targetOpacity = lowOp;
            }

            if (targetOpacity !== this.state.labelStyle.opacity) {
                this.tweenState('labelOpacity', {
                    easing: tweenState.easingTypes.easeInOutQuad,
                    duration: 1000,
                    endValue: targetOpacity
                });
            }
        },

        _addArmy: function() {
            var self = this;
            var newNode = self.refs.armyCount.getDOMNode().cloneNode(true);
            self.getDOMNode().appendChild(newNode);
            VelocityJS(newNode, {
                translateY: "-50px",
                opacity: 0
            }, 500).then(function() {
                self.getDOMNode().removeChild(newNode);
            });
            self._refreshState();
        },

        _removeArmy: function() {
            var self = this;
            var newNode = self.refs.armyCount.getDOMNode().cloneNode(true);
            self.getDOMNode().appendChild(newNode);
            VelocityJS(newNode, {
                translateY: "50px",
                opacity: 0
            }, 500).then(function() {
                self.getDOMNode().removeChild(newNode);
            });
            self._refreshState();
        },

        _toggleFilter: function(filter, state) {
            var newState = {
                style: {
                    filter: {
                        $set: state ? 'url(#' + filter + ')' : ''
                    }
                }
            };
            this.setState(React.addons.update(this.state, newState));
        },

        _setFill: function(colour) {
            var newState = {
                style: {
                    fill: {
                        $set: colour
                    }
                }
            };
            this.setState(React.addons.update(this.state, newState));
        },

        _setOpacity: function(value) {
            var newState = {
                style: {
                    opacity: {
                        $set: value
                    }
                }
            };
            this.setState(React.addons.update(this.state, newState));
        },

        _click: function() {
            if (this.firstClick) {

                var countryRequestState = ServerRequest.getCountryDerivedInformation(this.props.countryName);

                if (countryRequestState.option) {
                    ServerActions.selectTerritory(this.props.countryName);
                }

            } else {

                this.firstClick = true;
                ViewActions.selectCountry(this.props.countryName);
                setTimeout(function() {
                    this.firstClick = false
                }.bind(this), 500);
            }
        },

        render: function() {
            var self = this;
            var style = this.state.style;
            //console.log("hi")
            return (
                <g onClick={ self._click } transform={ "scale(1,1) translate(" + self.state.offsetX + "," + self.state.offsetY + ")" }>
                    <path style={ style } d={ self.state.path }></path>
                    <g ref="armyCount">
                        <circle cx={ self.state.labelX } cy={ self.state.labelY } r="14" opacity={ this.getTweeningValue('labelOpacity') } style={ self.state.labelStyle } />
                        <text x={ self.state.labelX - 7 } y={ self.state.labelY + 5 } fill="black">{ self.state.armies }</text>
                    </g>
                </g>
            )
        }
    });



//setLabelPos2: function() {
//    var pathSegList = this.getDOMNode().children[0].pathSegList;
//
//    var avg = {labelX: 0, labelY: 0};
//
//    // loop through segments, adding each endpoint to the restored dataset
//    for (var i = 0; i < pathSegList.length; i++) {
//        if (pathSegList[i].x && pathSegList[i].y) {
//            avg.labelX += pathSegList[i].x;
//            avg.labelY += pathSegList[i].y;
//        }
//    }
//
//    avg.labelX = avg.labelX / pathSegList.length;
//    avg.labelY = avg.labelY / pathSegList.length;
//
//    this.setState(avg);
//},
//
//setLabelPos3: function() {
//    var path = this.getDOMNode().children[0];
//
//    var avg = {labelX: 0, labelY: 0};
//
//    // loop through segments, adding each endpoint to the restored dataset
//    var i = 0;
//    var l = path.getTotalLength();
//    for (var n = 0; n < l; n += l/1000) {
//        var point = path.getPointAtLength(n);
//        avg.labelX += point.x;
//        avg.labelY += point.y;
//        i++;
//    }
//
//    avg.labelX = avg.labelX / i;
//    avg.labelY = avg.labelY / i;
//
//    this.setState(avg);
//},

