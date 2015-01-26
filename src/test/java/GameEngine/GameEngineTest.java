package GameEngine;

import GameBuilders.DemoGame;
import GameState.State;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        GameEngine gameThr = new GameEngine(gameState);
        gameThr.run();
    }
    
    @Test
    public void testJsonify() {

        Lobby t = new Lobby();
        System.out.println(Jsonify.getObjectAsJsonString(t));
//        System.out.println(Jsonify.getObjectAsJsonString(new Territory("lol")));
    }
}