var React = require('react/addons'),
    StateStore = require('../../stores/StateStore');

var renderings = {
    GameStart: React.createClass({
        render: function () {
            return <div>Game has started.</div>
        }
    }),

    PlayerChange: React.createClass({
        render: function () {
            return <div>It's { this.props.meta.actingPlayerId }'s go.</div>
        }
    }),

     ArmyHandout: React.createClass({
    render: function () {
        return <div>{ this.props.meta.actingPlayerId } got { this.props.meta.amount } armies.</div>
        }
    }),

    ArmyPlacement: React.createClass({
    render: function () {
        return <div>{ this.props.meta.actingPlayerId } placed { this.props.meta.amount } armies on { this.props.meta.targetId }.</div>
        }
    }),

    FightResult: React.createClass({
    render: function () {
        return <div>{ this.props.meta.attackerId } attacked { this.props.meta.defenderId } with dice { this.props.meta.attackDiceRolled } vs { this.props.meta.defendDiceRolled }.</div>
        }
    })
};

module.exports = React.createClass({

    getInitialState: function() {
        return {
            changes: []
        }
    },

    componentDidMount: function() {
        StateStore.addChangeListener("change_happened", this._pushChange);
    },

    _pushChange: function() {
        this.state.changes.push(StateStore.getChange());
    },

    render: function() {
        var number = this.props.value ? this.props.value : 1,
            self = this;

        return (
            <div className="log">
            {
                this.state.changes.map(function (change) {
                    return React.createElement(renderings[change.changeType], { meta: change });
                })
            }
            </div>
        )
    }
});

//<Country countryName={ self.props.countryName } />
