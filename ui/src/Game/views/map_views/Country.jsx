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
            obj.fill = "#D3D930";
            return obj;
        },

        componentDidMount: function() {
            if (this.props.onMap) {
                StateStore.addChangeListener("state_change", this._refreshState);
                StateStore.addChangeListener("add_army:" + this.props.countryName, this._refreshState);
                StateStore.addChangeListener("remove_army:" + this.props.countryName, this._refreshState);
                ServerRequest.addChangeListener("request", this._refreshState);
                this.setLabelPos();
            }
            this._refreshState();
        },

        componentWillUnmount: function() {
            if (this.props.onMap) {
                StateStore.removeChangeListener("state_change", this._refreshState);
                StateStore.removeChangeListener("add_army:" + this.props.countryName, this._refreshState);
                StateStore.removeChangeListener("remove_army:" + this.props.countryName, this._refreshState);
                ServerRequest.removeChangeListener("request", this._refreshState);
            }
            //ViewStore.removeChangeListener(this.props.countryName, this._refreshState);
        },

        setLabelPos: function() {
            if (this.getDOMNode().getBBox) {
                var bBox = this.getDOMNode().getBBox();

                this.setState({
                    labelX: (bBox.width / 2),
                    labelY: (bBox.height/2)
                });
            }
        },

        shouldComponentUpdate: function() {
            return true;
        },

        _refreshState: function() {

            var countryState = StateStore.getCountryState(this.props.countryName);
            var countryRequestState = ServerRequest.getCountryDerivedInformation(this.props.countryName);

            var newState = {
                armies: countryState.armies,
                isOption: countryRequestState.option
            };


            if (countryState.owner) {
                newState.fill = '#' + StateStore.getPlayerState(countryState.owner).colour;
            }

            this.setState(newState);

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
            var cx = React.addons.classSet;
            var transform = this.props.onMap ?  "scale(1,1) translate(" + self.state.offsetX + "," + self.state.offsetY + ")" : "";
            var labelClasses = { country_label: true, can_choose: self.state.isOption };
            labelClasses[self.state.continent] = true;
            var textCentering = 6 + (self.state.armies > 9 ? 6 : 0) + (self.state.armies >= 99 ? 6 : 0);
            return (
                <g onClick={ self._click } transform={ transform }>
                    <path fill={ self.state.fill } className="country_style" d={ self.state.path }></path>
                    {
                        this.props.onMap ? <g ref="armyCount" className={ cx( labelClasses ) }>
                            <circle cx={ self.state.labelX } cy={ self.state.labelY } r="20" />
                            <text x={ self.state.labelX - textCentering } y={ self.state.labelY + 6 } fill="black">{ self.state.armies }</text>
                        </g> : ''
                    }
                </g>
            );
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

