package UIHelpers;

import LobbyServer.LobbyServer;
import org.java_websocket.WebSocketImpl;

import java.io.IOException;

/**
 * Created by bt on 20/04/2015.
 */
public class LobbyRunner {

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


}
