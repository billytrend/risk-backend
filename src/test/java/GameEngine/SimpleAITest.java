package GameEngine;

import GameBuilders.RiskMapGameBuilder;
import GameState.State;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.minlog.Log;

import static com.esotericsoftware.minlog.Log.debug;

public class SimpleAITest {


    @Before
    public void setUp() throws Exception {
//        Log.NONE();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDumbBot() throws InterruptedException {
	    PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
	    		new DumbBotInterface(), new DumbBotInterface()};

        State gameState = RiskMapGameBuilder.buildGame(interfaces);


        //gameState.setPlayers(playerList);
        Log.DEBUG = true;
        
        gameState.print();

        GameEngine gameThr = new GameEngine(gameState);
        Thread gameThread = new Thread(gameThr);
        gameThread.start();
        gameThread.join();
        gameState.print();
        // TODO: add assertions about state.
    }
    
    @Test
    public void testJsonify() {

        Lobby t = new Lobby();
        debug(Jsonify.getObjectAsJsonString(t));
//        debug(Jsonify.getObjectAsJsonString(new Territory("lol")));
    }
}