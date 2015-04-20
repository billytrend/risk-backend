var React = require('react/addons'),
    Store = require('../Store'),
    Actions = require('../Actions'),
    Player = require('./Player.jsx');


module.exports = React.createClass({


    getInitialState: function() {
        return {
            ais: [],
            humans: [],
            name: ""
        };
    },

    _selectGame: function() {
        Actions.joinGame(this.props.index);
    },

    _push: function() {
        if (this.state.ais.length + this.state.humans.length < 6) {
            var val = this.refs.newGame.getDOMNode().value;
            if (val == "Human") {
                this.state.humans.push(val);
            } else {
                this.state.ais.push(val);
            }
            this.setState(this.state);

        }
    },

    _pop: function(index) {
        this.state.ais.splice(index, 1);
        this.setState(this.state);
    },

    _startWaiting: function() {
        Actions.createGame(this.state.name, this.state.humans.length, this.state.ais);
    },

    render: function() {
        var state = this.state;

        var self = this;

        return (
            <div className="game">
                <input className="gameId" placeholder="Create Game">{ this.state.name }</input>
                {
                    state.ais.concat(this.state.humans).map(function(ai, i) {
                        return <div>{ ai }<button onClick={self._pop.bind(null, i)} >-</button></div>;
                    })
                }
                <div>
                    <select ref="newGame">
                        <option value="Human">Human</option>
                        <option value="Berserker">Berserker</option>
                        <option value="BorderControl">Border Control</option>
                        <option value="CommunistAggressive">Communist Aggressive</option>
                        <option value="CommunistDefensive">Communist Offensive</option>
                    </select>
                    <button onClick={ self._push }>+</button>
                </div>
                <div>
                    <button onClick={ self._startWaiting }>Start Waiting</button>
                </div>
            </div>
        );
    }

});