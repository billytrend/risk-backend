window.$ = require('jquery');
window.jQuery = $;
require("../index.html");
require("./main.css");
require("./lobby/lobby.css");
require('./Common/dispatcher.js');

var React = require('react'),
    Game = require('./Game/Root.jsx'),
    Lobby = require('./lobby/views/LobbyView.jsx'),
    Store = require('./lobby/Store');

React.render(
    <Game />,
    document.getElementById('root')
);