//package PeerServer.server;
//
//import GameBuilders.RiskMapGameBuilder;
//import GameEngine.GameEngine;
//import GameState.Player;
//import GameState.State;
//import GameState.Territory;
//import GameUtils.ArmyUtils;
//import GameUtils.PlayerUtils;
//import GameUtils.TerritoryUtils;
//import GeneralUtils.Jsonify;
//import PeerServer.protocol.GenericProtocolObj;
//import PeerServer.protocol.cards.deploy;
//import PeerServer.protocol.cards.play_cards;
//import PeerServer.protocol.gameplay.*;
//import PeerServer.protocol.general.acknowledgement;
//import PeerServer.protocol.setup.*;
//import PlayerInput.RemotePlayer;
//
//import java.util.ArrayList;
//
//
//public class ClientProtocol {
//
//    private ProtocolState protocolState = ProtocolState.JOINING;
//    private PeerConnection host;
//    private ArrayList<Player> players = new ArrayList<Player>();
//    int myId;
//    State gameState;
//    attack currentAttack;
//
//    public ClientProtocol(PeerConnection host) {
//        this.host = host;
//    }
//
//    public ProtocolState iterateProtocol(String message) {
//
//        switch (protocolState) {
//            case JOINING: protocolState =  handleJoin(message); return protocolState;
////            case PINGING: protocolState =  handlePing(message); return protocolState;
//            case SETTING_UP: protocolState =  handleSetup(message); return protocolState;
//            case PLAY_CARD: protocolState =  handlePlayCard(message); return protocolState;
//            case DEPLOY: protocolState =  handleDeploy(message); return protocolState;
//            case DEFEND: protocolState =  handleDefend(message); return protocolState;
//            case BATTLE_CONF: protocolState =  handleBattleConf(message); return protocolState;
//            case FORTIFY: protocolState =  handleFortify(message); return protocolState;
//        }
//        return null;
//
//    }
//
//
//
//    protected ProtocolState handleJoin(String message) {
//
//        // send request to join the game
//        String name = "test";
//
//        join_game join = new join_game(new Integer[]{1}, new String[]{}, name, "key");
//
//        host.sendCommand(Jsonify.getObjectAsJsonString(join));
//
//        accept_join_game acceptJoin = (accept_join_game) Jsonify.getJsonStringAsObject(message, accept_join_game.class);
//
//        myId = acceptJoin.payload.player_id;
//
//        getOtherPlayers();
//
//        return ProtocolState.SETTING_UP;
//
//    }
//
//    protected void getOtherPlayers() {
//
//        String message = "";
//
//        while (true) {
//
//            message = host.receiveCommand();
//
//            if (!getCommandType(message).equals("players_joined")) break;
//
//            players_joined join = (players_joined) Jsonify.getJsonStringAsObject(message, players_joined.class);
//        }
//
//        while (true) {
//
//            ping ping = (ping) Jsonify.getJsonStringAsObject(message, ping.class);
//
//            players.add(new Player(new RemotePlayer(), ping.player_id));
//
//            message = host.receiveCommand();
//
//            if (!getCommandType(message).equals("ping")) break;
//
//        }
//
//        ready ready = (ready) Jsonify.getJsonStringAsObject(message, ready.class);
//
//        acknowledge(ready.ack_id);
//
//        gameState = new State();
//
//        RiskMapGameBuilder.addRiskTerritoriesToState(gameState);
//
//        gameState.setPlayers(players);
//
//        GameEngine g = new GameEngine(gameState);
//
//        Thread thr = new Thread(g);
//
//        thr.start();
//
//        try {
//            g.wait.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//
//    private ProtocolState handleSetup(String command) {
//
//        if (PlayerUtils.getCurrentPlayer(gameState).getNumberId() == myId) {
//
//        }
//
//        setup setup = (setup) Jsonify.getJsonStringAsObject(command, setup.class);
//
//        acknowledge(setup.ack_id);
//
//        notifyPlayerInterface(setup, setup.player_id);
//
//        if (ArmyUtils.somePlayerHasUndeployedArmies(gameState)) {
//            return ProtocolState.SETTING_UP;
//        }
//
//        return ProtocolState.PLAY_CARD;
//
//    }
//
//    private void notifyPlayerInterface(setup setup, int player_id) {
//        Territory t = TerritoryUtils.getTerritoryWithId(gameState, setup.payload);
//        Player p = PlayerUtils.getPlayerWithId(gameState, player_id);
//        ((RemotePlayer) p.getCommunicationMethod()).responses.add(t);
//    }
//
//    private void notifyPlayerInterface(Object setup, int player_id) {
//
//    }
//
//
//    private ProtocolState handlePlayCard(String command) {
//
//        if (PlayerUtils.getCurrentPlayer(gameState).getNumberId() == myId) {
//            // TODO : make my move and send and return
//        }
//
//        //parse comamnd into play_cards object
//        play_cards cardPlay = (play_cards) Jsonify.getJsonStringAsObject(command, play_cards.class);
//
//        acknowledge(cardPlay.ack_id);
//
//        notifyPlayerInterface(cardPlay, cardPlay.player_id);
//
//        return ProtocolState.DEPLOY;
//
//    }
//
//    private ProtocolState handleDeploy(String command) {
//
//        if (PlayerUtils.getCurrentPlayer(gameState).getNumberId() == myId) {
//            // TODO : make my move and send and return
//        }
//
//        //parse command into deploy object
//        deploy deploy = (deploy) Jsonify.getJsonStringAsObject(command, deploy.class);
//
//        acknowledge(deploy.ack_id);
//
//        notifyPlayerInterface(deploy, deploy.player_id);
//
//        return ProtocolState.ATTACK;
//
//    }
//
//    private ProtocolState handleAttack(PeerConnection p, String command) {
//
//        if (PlayerUtils.getCurrentPlayer(gameState).getNumberId() == myId) {
//            // TODO : make my move and send and return
//        }
//
//        //parse command into deploy object
//        currentAttack = (attack) Jsonify.getJsonStringAsObject(command, attack.class);
//
//        if (currentAttack == null) {
//            return ProtocolState.FORTIFY;
//        }
//
//        acknowledge(currentAttack.ack_id);
//
//        //notify interface
//        notifyPlayerInterface(currentAttack, currentAttack.player_id);
//
//        if (currentAttack.player_id == myId) {
//            defendSelf(currentAttack);
//        }
//
//        return ProtocolState.DEFEND;
//    }
//
//    private void defendSelf(attack attack) {
//        // TODO: get move
//
//    }
//
//    private ProtocolState handleDefend(String command) {
//
//        //parse command into deploy object
//        defend defend = (defend) Jsonify.getJsonStringAsObject(command, defend.class);
//
//        acknowledge(defend.ack_id);
//
//        //notify interface
//        notifyPlayerInterface(defend, defend.player_id);
//
//        carryOutDiceRoll(defend, currentAttack);
//
//        return ProtocolState.BATTLE_CONF;
//
//    }
//
//    private ProtocolState handleBattleConf(String command) {
//
//        //parse command into deploy object
//        attack_capture attack_capture = (attack_capture) Jsonify.getJsonStringAsObject(command, attack_capture.class);
//
//        acknowledge(attack_capture.ack_id);
//
//        return ProtocolState.FORTIFY;
//    }
//
//    private ProtocolState handleFortify(String command) {
//
//        if (PlayerUtils.getCurrentPlayer(gameState).getNumberId() == myId) {
//            // TODO : make my move and send and return
//        }
//
//        //parse command into deploy object
//        fortify fortify = (fortify) Jsonify.getJsonStringAsObject(command, fortify.class);
//
//        acknowledge(fortify.ack_id);
//
//        //notify interface
//        notifyPlayerInterface(fortify, fortify.player_id);
//
//        return ProtocolState.PLAY_CARD;
//    }
//
//    private boolean acknowledge(int ackId) {
//
//        acknowledgement acknowledgement = new acknowledgement(myId, ackId);
//
//        host.sendCommand(Jsonify.getObjectAsJsonString(acknowledgement));
//
//        return true;
//    }
//
//    private int carryOutDiceRoll(defend defend, attack currentAttack) {
//        return 0;
//    }
//
//    private int carryAttack() {
//        return 0;
//    }
//
//
//    public static String getCommandType(String s) {
//        return ((GenericProtocolObj) Jsonify.getJsonStringAsObject(s, GenericProtocolObj.class)).command;
//    }
//}
