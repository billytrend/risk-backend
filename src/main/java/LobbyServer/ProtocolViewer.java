package LobbyServer;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.Response;
import LobbyServer.LobbyState.PlayerConnection;
import PeerServer.server.ClientProtocol;
import PeerServer.server.HostProtocol;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import static com.esotericsoftware.minlog.Log.debug;

public class ProtocolViewer extends WebSocketServer {

    private ArrayList<Player> players = new ArrayList<Player>();
    private Player singlePlayer;
    private State gameState;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        resource_handler.setResourceBase("./ui-build");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
        server.setHandler(handlers);

        server.start();

        ProtocolViewer p = new ProtocolViewer(8887);
        p.start();
    }

    public ProtocolViewer(int port) throws UnknownHostException {
        super(new InetSocketAddress( port ) );
        gameState = new State();
        RiskMapGameBuilder.addRiskTerritoriesToState(gameState);

    }

    /**
     * Whenever a client connects to the server for the first time, their 'WebSocket' object is added to the lobby.
     *
     * @param conn
     * @param handshake
     */
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        gameState.addGhost(new PlayerConnection(conn));
        System.out.println("hi!!!");
        HostProtocol p = new HostProtocol(gameState);
        Thread th = new Thread(p);
        th.start();
        
       // State s = new State();
     //   RiskMapGameBuilder.addRiskTerritoriesToState(s);
      //  ClientProtocol cP = new ClientProtocol(s);
      //  Thread thCP = new Thread(cP);
      //  thCP.start();
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        this.sendToAll( conn + " has left the room!" );
        debug(conn + " has left the room!");
    }

    /**
     * Whenever a client sends a message to the server, it is handled here.
     * These messages must be a valid json string and they must contain a field 'commandType'
     * which must equal the name of the java class that the object will be parsed to i.e. "ArmySelection"
     * The current implementation automatically transforms this json object into a java object of this type.
     * Magic huh?
     *
     * @param conn
     * @param message
     */
    @Override
    public void onMessage( WebSocket conn, String message ) {
        ClientMessage messageObject = WebServerUtils.getMessageObject(message);
        ((PlayerConnection) singlePlayer.getCommunicationMethod()).setLatestResponse((Response) messageObject);
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    public void sendToAll( String text ) {
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( "Lol!" );
            }
        }
    }
}