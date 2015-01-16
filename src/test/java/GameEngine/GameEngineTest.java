package GameEngine;

import static org.junit.Assert.*;
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
    public void testDumbBot() {
        State gameState = DemoGame.buildGame(4, 10);
        GameEngine gameThr = new GameEngine(gameState);
        gameThr.run();
        assertTrue(gameThr.getState().isEndOfGame());
        	
    }
    
    @Test
    public void testJsonify() {
        System.out.println(Jsonify.getObjectAsJsonString(new Territory("lol")));
    }
}