package GameEngine;

import GameBuilders.DemoGame;
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

    // This test causes an exception but at least it shows things are working!
    @Test
    public void testDumbBot() throws Exception {
        State gameState = DemoGame.buildGame();
        GameEngine.play(gameState);
    }
}