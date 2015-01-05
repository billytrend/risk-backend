package GameEngine;

import GameState.GameBuilders.DemoGame;
import GameState.State;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameEngineTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDumbBot() throws Exception {
        State gameState = DemoGame.buildGame();
        GameEngine.play(gameState);
    }
}