package AI;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.BorderControl;
import PlayerInput.CommunistAggressive;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import UIHelpers.SingleGameRunner;
import com.esotericsoftware.minlog.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class BorderControlTest {


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCommunist() throws InterruptedException {
        Log.NONE();

        PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
	    		new DumbBotInterface(), new DumbBotInterface()};

        State gameState = RiskMapGameBuilder.buildGame(interfaces);

        Player play1 = new Player(new BorderControl(gameState));
        Player play2 = new Player(new CommunistAggressive(gameState));
        Player play3 = new Player(new DumbBotInterface());
        Player play4 = new Player(new DumbBotInterface());

        ArrayList<Player> playerList = new ArrayList<Player>();
        playerList.add(play1);
        playerList.add(play2);
        playerList.add(play3);
        playerList.add(play4);

        gameState.setPlayers(playerList);
//        Log.DEBUG = true;

        gameState.print();
        SingleGameRunner s = new SingleGameRunner(playerList, gameState);
        try {
            s.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: add assertions about state.
    }

    @Test
    public void testJsonify() {

        Lobby t = new Lobby();
//        debug(Jsonify.getObjectAsJsonString(t));
//        debug(Jsonify.getObjectAsJsonString(new Territory("lol")));
    }}
