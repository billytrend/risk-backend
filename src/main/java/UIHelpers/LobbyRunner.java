package UIHelpers;

import LobbyServer.LobbyServer;
import org.java_websocket.WebSocketImpl;

import java.awt.*;
import java.net.InetAddress;
import java.net.URI;

/**
 * Created by bt on 20/04/2015.
 */
public class LobbyRunner {

    public static void main( String[] args ) throws Exception {
        WebSocketImpl.DEBUG = false;
        int wsport = 8887;
        int staticport = 8080;
        try {
            wsport = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }

        new StaticServer();
        LobbyServer s = new LobbyServer( wsport );
        s.start();

        try {
            Desktop.getDesktop().browse(new URI("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + staticport));
        } catch (Exception e) {
            System.out.println("Tried to open up your default browser but failed. Access the game at http://localhost:8080");
        }
    }


}
