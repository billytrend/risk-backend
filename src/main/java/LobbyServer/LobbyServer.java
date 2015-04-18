package LobbyServer;

import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.GameDescription;
import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.LobbyUtils;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.Response;
import LobbyServer.LobbyState.ObjectFromClient.JoinGameReq;
import LobbyServer.LobbyState.PlayerConnection;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import static com.esotericsoftware.minlog.Log.debug;

public class LobbyServer extends WebSocketServer {
    
    public static void main( String[] args ) throws InterruptedException , IOException {
        WebSocketImpl.DEBUG = false;
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }
        LobbyServer s = new LobbyServer( port );
        s.start();
    }

    private Lobby lobby = new Lobby();

    public LobbyServer(int port) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public LobbyServer(InetSocketAddress address) {
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

        // push player into lobby
        LobbyUtils.addConnection(this.lobby, conn);

        LobbyUtils.addGame(lobby, new GameDescription("test", 2));

        // send the lobby state to the player
        conn.send(Jsonify.getObjectAsJsonString(lobby));

    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        // replace connPlayer with AI
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

        // extract message
        ClientMessage messageObject = WebServerUtils.getMessageObject(message);

        // if game response, route to game
        if (messageObject instanceof Response) {
            LobbyUtils.routeMessage(lobby, conn, messageObject);
        }

        // if game description create, add to lobby
        if (messageObject instanceof GameDescription) {
            LobbyUtils.addGameDescription(lobby, messageObject);
            LobbyUtils.updateStateForLobbiedPlayers(lobby);
        }

        // if join game, add to game and send confirmation with game meta
        if (messageObject instanceof JoinGameReq) {
            if (LobbyUtils.addPlayerToGame(lobby, ((JoinGameReq) messageObject).gameIndex, conn)) {
                LobbyUtils.confirmJoined(conn, ((JoinGameReq) messageObject).gameIndex);
            }
            LobbyUtils.updateStateForLobbiedPlayers(lobby);
            LobbyUtils.startGames(lobby);
        }

        debug("RECD :::" + message);
        PlayerConnection player = LobbyUtils.getPlayer(lobby, conn);
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