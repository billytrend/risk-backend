package LobbyServer;

import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.PlayerConnection;
import LobbyServer.LobbyUtils.LobbyUtils;
import PlayerInput.PlayerChoice.Choice;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * This is the websocket server which handles the players whether they're playing or waiting to play
 */
public class GameServer extends WebSocketServer {
    
    public static void main( String[] args ) throws InterruptedException , IOException {
        WebSocketImpl.DEBUG = true;
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }
        GameServer s = new GameServer( port );
        s.start();
        System.out.println( "ChatServer started on port: " + s.getPort() );
    }

    private Lobby lobby = new Lobby();
    
    public GameServer(int port) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public GameServer(InetSocketAddress address) {
        super( address );
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        // new person has connected :D
        this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        this.sendToAll( conn + " has left the room!" );
        System.out.println( conn + " has left the room!" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        ClientMessage messageObject = WebServerUtils.getMessageObject(message);
        PlayerConnection player = LobbyUtils.getPlayer(lobby, conn);
        if (messageObject instanceof Choice) {
            player.setChoice((Choice) messageObject);
        }
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