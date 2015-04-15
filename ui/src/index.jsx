window.$ = require('jquery');
window.jQuery = $;
require("../index.html");
require("./main.css");
var React = require('react'),
    Game = require('./Game/Root.jsx');

React.render(
    <Game />,
    document.getElementById('root')
);