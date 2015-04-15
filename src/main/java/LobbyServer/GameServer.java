package LobbyServer;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.State;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.Response;
import LobbyServer.LobbyState.PlayerConnection;
import LobbyServer.LobbyUtils.LobbyUtils;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import static com.esotericsoftware.minlog.Log.debug;

public class GameServer extends WebSocketServer {
    
    public static void main( String[] args ) throws InterruptedException , IOException {
        WebSocketImpl.DEBUG = false;
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }
        GameServer s = new GameServer( port );
        s.start();
    }

    private Lobby lobby = new Lobby();

    public GameServer(int port) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public GameServer(InetSocketAddress address) {
        super( address );
    }

    /**
     * Whenever a client connects to the server for the first time, their 'WebSocket' object is added to the lobby.
     *
     * @param conn
     * @param handshake
     */
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        debug("hi!");
        PlayerConnection player = new PlayerConnection(conn);
        LobbyUtils.addConnection(this.lobby, conn, player);
        debug(Jsonify.getObjectAsJsonString(new Lobby()));
        PlayerInterface[] interfaces = new PlayerInterface[]{player, new DumbBotInterface(), new DumbBotInterface(), new DumbBotInterface()};
        State gameState = RiskMapGameBuilder.buildGame(interfaces);
        GameEngine game = new GameEngine(gameState);
        Thread gameThr = new Thread(game);
        System.out.println(Jsonify.getObjectAsJsonString(gameState));
        conn.send(Jsonify.getObjectAsJsonString(gameState));
        gameThr.start();
        debug("Hi!");

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
        debug("RECD :::" + message);
        ClientMessage messageObject = WebServerUtils.getMessageObject(message);
        PlayerConnection player = LobbyUtils.getPlayer(lobby, conn);
        if (messageObject instanceof Response) {
            LobbyUtils.routeMessage(lobby, conn, messageObject);
        }
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        conn.send("no, sorry");
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