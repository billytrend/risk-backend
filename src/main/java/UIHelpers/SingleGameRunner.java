package UIHelpers;


import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.SingleGameServer;
import PlayerInput.*;

import java.util.ArrayList;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

public class SingleGameRunner {

    ArrayList<Player> players = new ArrayList<Player>();
    State gameState;

    public static void main(String[] args) throws Exception {
        ArrayList<Player> players = new ArrayList<Player>();
        State gameState = RiskMapGameBuilder.buildGame(null);
        //pink
        players.add(0, new Player(new CommunistDefensive(gameState)));
        //blue
        players.add(1, new Player(new Billy(gameState)));
        //purple
        players.add(1, new Player(new BorderControl(gameState)));
        //green
        //players.add(2, new Player(new DumbBotInterface()));
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

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        resource_handler.setResourceBase("./ui-build/");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();

        SingleGameServer s = new SingleGameServer(8887, gameState, players);
        server.join();


    }

}