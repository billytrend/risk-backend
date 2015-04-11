package UIHelpers;

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
}
