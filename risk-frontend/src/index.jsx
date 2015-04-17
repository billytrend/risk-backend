window.$ = require('jquery');
window.jQuery = $;
var React = require('react'),
    Game = require('./Game/Root.jsx');

React.render(
    <Game />,
    document.getElementById('root')
);