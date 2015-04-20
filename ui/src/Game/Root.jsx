var Inspector = require('./views/Inspector.jsx'),
    Map = require('./views/Map.jsx'),
    WorldMap = require('./views/WorldMap.jsx')
    PlayerOverview = require('./views/PlayerOverview.jsx'),
    React = require('react'),
    Controls = require('./views/Controls.jsx'),
    DieBox = require('./views/map_views/DieBox.jsx'),
    Cards = require('./views/map_views/CardBox.jsx'),
    EndGo = require('./views/map_views/EndGo.jsx'),
    Prompt = require('./views/Prompt.jsx'),
    ArmySelection = require('./views/map_views/ArmySelection.jsx'),
    ServerRequest = require('./stores/ServerRequest'),
    StateStore = require('./stores/StateStore'),
    SoundTrack = require('./views/Audio.jsx'),
    Lobby = require('../lobby/views/LobbyView.jsx');

module.exports = React.createClass({

    getInitialState: function() {
        return {}
    },

    componentDidMount: function () {
        ServerRequest.addChangeListener('request', this._setViewSetup)
        ServerRequest.addChangeListener('clear', this._setViewSetup)
    },

    _setViewSetup: function() {
        this.setState({
            type: ServerRequest.getRequestType(),
            meta: ServerRequest.getRequestMeta()
        });
    },

    render: function() {
        var self = this;
        return <div>
            { StateStore.gameStarted() ? '' : <Lobby /> }
            <SoundTrack />
            <Prompt />
            <div className="main_view">
                <PlayerOverview />
                <WorldMap />
            </div>
            {
                (function () {
                    switch (self.state.type) {
                        case 'number_of_dice':
                            return <DieBox />
                            break;
                        case 'number_of_armies':
                            return <ArmySelection max={ self.state.meta.max } />
                            break;
                        case 'territory':
                            if(self.state.meta.canResign) return <EndGo canResign={ self.state.meta.canResign }/>
                            break;
                        case 'card':
                            return <Cards cardMeta={ self.state.meta } />
                        default:
                            break;
                    }
                })()
            }
        </div>
        }
});

//<div className="row">
//    <div className="col-md-8">
//    </div>
//    <div className="col-md-4">
//        <Inspector />
//    </div>
//</div>
//<div className="row">
//<div className="col-md-8">
//    <PlayerOverview />
//</div>
//<div className="col-md-4">
//<Controls />
//</div>
//</div>
