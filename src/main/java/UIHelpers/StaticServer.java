package UIHelpers;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

import java.io.File;
import java.net.BindException;

public class StaticServer {

    public StaticServer() {

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        ClassLoader loader = this.getClass().getClassLoader();
        File indexLoc = new File(loader.getResource("index.html").getFile());
        String htmlLoc = indexLoc.getParentFile().getAbsolutePath();

        System.out.println(htmlLoc);
        resource_handler.setResourceBase(htmlLoc);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
        server.setHandler(handlers);

        try {
            server.start();
        } catch (BindException e) {
            System.out.println("Tried to run on port 8080 but failed. Maybe you have something else on that port?");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
