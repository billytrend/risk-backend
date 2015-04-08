package GameEngine;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.CommunistAggressive;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;

import static com.esotericsoftware.minlog.Log.debug;

public class CommunistTest {


    @Before
    public void setUp() throws Exception {
//        Log.NONE();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCommunist() throws InterruptedException {
	    PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
	    		new DumbBotInterface(), new DumbBotInterface()};

        State gameState = RiskMapGameBuilder.buildGame(interfaces);

        Player play1 = new Player(new CommunistAggressive(gameState));
        Player play2 = new Player(new CommunistAggressive(gameState));
        Player play3 = new Player(new DumbBotInterface());
        Player play4 = new Player(new DumbBotInterface());

        ArrayList<Player> playerList = new ArrayList<Player>();
        playerList.add(play1);
        playerList.add(play2);
        playerList.add(play3);
        playerList.add(play4);

        gameState.setPlayers(playerList);
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