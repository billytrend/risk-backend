package GameEngine;

import GameBuilders.DemoGame;
import GameState.State;
import GameState.Territory;
import GeneralUtils.Jsonify;
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
        GameEngine gameThr = new GameEngine(gameState);
        gameThr.run();
    }
    
    @Test
    public void testJsonify() {
        System.out.println(Jsonify.getObjectAsJsonString(new Territory("lol")));
    }
}