package UIHelpers;


import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.SingleGameServer;
import PlayerInput.CommunistAggressive;
import PlayerInput.DumbBotInterface;
import PlayerInput.TheLoser;
import PlayerInput.*;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

import java.util.ArrayList;

public class SingleGameRunner {

    ArrayList<Player> players = new ArrayList<Player>();
    State gameState;

    public static void main(String[] args) throws Exception {
        ArrayList<Player> players = new ArrayList<Player>();
        State gameState = RiskMapGameBuilder.buildGame(null);
        
        //pink
        players.add(0, new Player(new SuperSwapper(gameState), "The test subject"));
        //blue
        players.add(1, new Player(new TheLoser(gameState), "Loser1"));
        //purple
        players.add(2, new Player(new TheLoser(gameState), "Loser2"));

        //green
        players.add(2, new Player(new DumbBotInterface()));
        //yellow
        //players.add(3, new Player(new DumbBotInterface()));

        SingleGameRunner s = new SingleGameRunner(players, gameState);
        s.start();
        

        
    }


    public SingleGameRunner(ArrayList<Player> players, State gameState) {
        this.players = players;
        this.gameState = gameState;
    }

    public void start() throws Exception {

        new StaticServer();
        SingleGameServer s = new SingleGameServer(8887, gameState, players);
        

    }

}