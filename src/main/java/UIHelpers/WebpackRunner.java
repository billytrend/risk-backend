package UIHelpers;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class WebpackRunner implements Runnable {
    @Override
    public void run() {
        try {
            Process p = Runtime.getRuntime().exec("npm run develop --prefix ./risk-frontend/");
            p.waitFor();
        }
        catch (Exception err) {
            err.printStackTrace();
        }

    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
