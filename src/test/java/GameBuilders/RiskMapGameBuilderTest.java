package GameBuilders;

import GameState.State;
import org.jgrapht.ext.DOTExporter;
import org.junit.Test;

import java.io.PrintWriter;

public class RiskMapGameBuilderTest {

//    @Test
//    public void testDumbBot() throws InterruptedException {
////        Log.DEBUG();
//        PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
//                new DumbBotInterface(),new DumbBotInterface(),new DumbBotInterface(),new DumbBotInterface()};
//        State gameState = RiskMapGameBuilder.buildGame(14, interfaces);
//        GameEngine gameThr = new GameEngine(gameState);
//        Thread gameThread = new Thread(gameThr);
//        gameThread.start();
//        gameThread.join();
//    }
//

    @Test
    public void testPrintGameMap() {
        State s = new State();
        RiskMapGameBuilder.addRiskTerritoriesToState(s);
        DOTExporter exporter = new DOTExporter();
        exporter.export(new PrintWriter(System.out), s.getTerritories());
    }

}