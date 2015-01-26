package GameEngine;

import static org.junit.Assert.*;
import GameBuilders.DemoGame;
import GameState.State;
import GeneralUtils.Jsonify;

import LobbyServer.LobbyState.Lobby;
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
    public void testDumbBot() {
        State gameState = DemoGame.buildGame(4, 10);
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