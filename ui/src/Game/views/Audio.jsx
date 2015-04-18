var React = require('react'),
    ServerRequest = require('../stores/ServerRequest'),
    FileLoader = require('file-loader');

var soundEffects = {
    "clash": require('../../Assets/sounds/clash.mp3'),
    "fiveArmies": require('../../Assets/sounds/five-armies.mp3')
};

module.exports = React.createClass({

    getInitialState: function() {
        return {
            message: "Waiting..."
        }
    },

    componentDidMount: function() {
        //ServerRequest.addChangeListener("request", this._playEffect.bind(this, 'clash'));
        //this._playTheme();
    },

    _playEffect: function(effectName) {
        this.refs[effectName].getDOMNode().play();
    },

    _playTheme: function() {
        this.refs.fiveArmies.getDOMNode().play();
    },

    render: function() {
        return <div>
            {
                Object.keys(soundEffects).map(function(effect) {
                    return <audio ref={ effect } src={ soundEffects[effect] }>
                        <source src={ soundEffects[effect] } type="audio/mp3" />
                    </audio>
                })
            }
        </div>
    }
});