package UIHelpers;

import LobbyServer.LobbyServer;
import org.java_websocket.WebSocketImpl;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

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

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(staticport);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        String webDir = LobbyRunner.class.getClassLoader().getResource("./").toExternalForm();
        resource_handler.setResourceBase(webDir);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
        server.setHandler(handlers);

        server.start();

        LobbyServer s = new LobbyServer( wsport );
        s.start();

        Desktop.getDesktop().browse(new URI("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + staticport));
    }


}
