package LobbyServer;

import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.Response;
import LobbyServer.LobbyState.PlayerConnection;
import PlayerInput.PlayerInterface;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import static com.esotericsoftware.minlog.Log.debug;

public class SingleGameServer extends WebSocketServer {

    private ArrayList<Player> players = new ArrayList<Player>();
    private Player singlePlayer;
    private State gameState;

    public SingleGameServer(int port, State gameState, PlayerInterface[] interfaces) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        for (PlayerInterface p : interfaces) {
            players.add(new Player(p, 30));
        }
        System.out.println(Jsonify.getObjectAsJsonString(gameState));
        this.start();
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
        singlePlayer = new Player(new PlayerConnection(conn), 123);
        players.add(singlePlayer);
        gameState.setPlayers(players);
        GameEngine game = new GameEngine(gameState);
        Thread gameThread = new Thread(game);
        gameThread.start();
        conn.send(Jsonify.getObjectAsJsonString(gameState));
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