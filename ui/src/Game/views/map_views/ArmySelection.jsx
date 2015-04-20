var React = require('react/addons'),
    Die = require('./Die.jsx'),
    ServerRequest = require('../../stores/ServerRequest'),
    ServerActions = require('../../actions/ServerActions'),
    Country = require('./Country.jsx');

var filters = {
    selected: 'selected',
    option: 'option'
};

var UpArrow = React.createClass({
    render: function() {
        return (
            <svg viewBox="0 0 1872 940" className="army_arrow">
                <g opacity={ this.props.activated ? 1 : 0.5 } onClick={ this.props.onClick } stroke="none" strokeWidth="1" fill="none"  fill-rule="evenodd" >
                    <polygon stroke="#979797" fill="#6C6C6C" points="936 1 1870 939 936 676.008356 2 939 "></polygon>
                </g>
            </svg>
        )
    }
});

var DownArrow = React.createClass({
    render: function() {
        return (
            <svg viewBox="0 0 1872 940" className="army_arrow">
                <g opacity={ this.props.activated ? 1 : 0.5 } onClick={ this.props.onClick } stroke="none" strokeWidth="1" fill="none"  fill-rule="evenodd" >
                    <path  d="M2,1 L936,263.991644 L1870,1 L936,939 L2,1 Z" id="Triangle-1" stroke="#979797" fill="#6C6C6C" ></path>
                </g>
            </svg>
        )
    }
});



module.exports = React.createClass({

    getInitialState: function() {
        return {
            number: 1
        };
    },

    componentDidMount: function() {
        ServerRequest.addChangeListener("request");
    },

    _toggleVisibility: function() {
        if (this.getDOMElement().style.visibility == "collapse") {
            this.getDOMElement().style.visibility = "visible";
        } else {

        }
    },

    _handleDiceRequest: function() {

    },

    _increase: function(amount) {
        var newAmount = this.state.number + amount;
        console.log(newAmount)
        if (newAmount <= this.props.max && newAmount >= 0) {
            this.setState({
                number: newAmount
            });
        }
    },

    submit: function() {
          ServerActions.selectArmies(this.state.number)
    },

    render: function() {
        var self = this;

        return (
            <div className="view_layer">
                <div className="dialogue_box">
                    <div className="army_selection_column">
                        <UpArrow activated={ self.state.number + 1 <= self.props.max } onClick={ self._increase.bind(self, 1) } />
                        <div>S</div>
                        <DownArrow activated={ self.state.number - 1 >= 0 }  onClick={ self._increase.bind(self, -1) } />
                    </div>
                    <div className="army_selection_column">
                        <UpArrow activated={ self.state.number + 5 <= self.props.max } onClick={ self._increase.bind(self, 5) } />
                        <div>H</div>
                        <DownArrow activated={ self.state.number - 5 >= 0 }  onClick={ self._increase.bind(self, -5) } />

                    </div>
                    <div className="army_selection_column">
                        <UpArrow activated={ self.state.number + 10 <= self.props.max } onClick={ self._increase.bind(self, 10) } />
                        <div>C</div>

                        <DownArrow activated={ self.state.number - 10 >= 0 }  onClick={ self._increase.bind(self, -10) }/>
                    </div>
                    <div className="army_selection_column">
                        <div className="caption">deploy</div>
                            { self.state.number }
                    </div>
                    <div className="army_selection_column">
                        <div className="caption">out of</div>
                            { self.props.max }
                    </div>
                    <div className="army_selection_column">
                        <button onClick={ self.submit }>Deploy</button>
                    </div>
                </div>
            </div>
        )
    }
});
