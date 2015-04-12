var React = require('react/addons'),
    MapRep = require('../../../Assets/map.json'),
    ViewStore = require('../../stores/ViewStore'),
    ViewActions = require('../../actions/ViewActions'),
    StateStore = require('../../stores/StateStore'),
    ServerRequest = require('../../stores/ServerRequest'),
    ServerActions = require('../../actions/ServerActions');

var arrangements = [
    [false, false, false, false, false, false, false],
    [false, false, false, true, false, false, false],
    [true, false, false, false, false, false, true],
    [true, false, false, true, false, false, true],
    [true, true, false, false, false, true, true],
    [true, false, true, true, true, false, true],
    [true, true, true, false, true, true, true]
];

module.exports = React.createClass({

    submit: function() {
        ServerActions.selectDice(this.props.index);
    },

    render: function() {
        var number = this.props.value ? this.props.value : 1,
            self = this;

        return (
            <svg onClick={ self.submit }>
                <g id={ "die-" + this.props.index } stroke="none" strokeWidth="1" fill="none" fillRule="evenodd">
                    <rect stroke="#979797" fill="#FFFFFF" x="1" y="1" width="91" height="91" rx="8"></rect>
                    <circle style={{ visibility: (arrangements[number][0] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="19" cy="19" r="8"></circle>
                    <circle style={{ visibility: (arrangements[number][1] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="73" cy="19" r="8"></circle>
                    <circle style={{ visibility: (arrangements[number][2] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="19" cy="46" r="8"></circle>
                    <circle style={{ visibility: (arrangements[number][3] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="46" cy="46" r="8"></circle>
                    <circle style={{ visibility: (arrangements[number][4] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="73" cy="46" r="8"></circle>
                    <circle style={{ visibility: (arrangements[number][5] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="19" cy="73" r="8"></circle>
                    <circle style={{ visibility: (arrangements[number][6] ? 'visible' : 'hidden') }} stroke="#979797" fill="black" cx="73" cy="73" r="8"></circle>
                </g>
            </svg>
        )
    }
});
