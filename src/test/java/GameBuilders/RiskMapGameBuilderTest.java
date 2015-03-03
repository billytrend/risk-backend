package GameBuilders;

import GameEngine.GameEngine;
import GameState.State;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import org.junit.Test;

public class RiskMapGameBuilderTest {

    @Test
    public void testDumbBot() throws InterruptedException {
//        Log.DEBUG();
        PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
                new DumbBotInterface(),new DumbBotInterface(),new DumbBotInterface(),new DumbBotInterface()};
        State gameState = RiskMapGameBuilder.buildGame(14, interfaces);
        GameEngine gameThr = new GameEngine(gameState);
//        Thread gameThread = new Thread(gameThr);
//        gameThread.start();
//        gameThread.join();
    }


}