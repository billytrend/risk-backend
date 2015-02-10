package GameEngine;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import com.esotericsoftware.minlog.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.esotericsoftware.minlog.Log.debug;

public class SimpleAITest {


    @Before
    public void setUp() throws Exception {
        Log.NONE();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDumbBot() {
        State gameState = DemoGameBuilder.buildGame(4, 10);
        GameEngine gameThr = new GameEngine(gameState);
        gameThr.run();
    }
    
    @Test
    public void testJsonify() {

        Lobby t = new Lobby();
        debug(Jsonify.getObjectAsJsonString(t));
//        debug(Jsonify.getObjectAsJsonString(new Territory("lol")));
    }
}