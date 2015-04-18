package AI;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.BorderControl;
import PlayerInput.CommunistAggressive;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import com.esotericsoftware.minlog.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class BorderControlTest {
	Player play1, play2, play3, play4;

	@Before
	public void setup() throws InterruptedException {

		PlayerInterface[] interfaces = new PlayerInterface[] {
				new DumbBotInterface(), new DumbBotInterface(),
				new DumbBotInterface(), new DumbBotInterface() };

		State gameState = RiskMapGameBuilder.buildGame(interfaces);

		play1 = new Player(new BorderControl(gameState));
		play2 = new Player(new CommunistAggressive(gameState));
		play3 = new Player(new DumbBotInterface());
		play4 = new Player(new DumbBotInterface());

		ArrayList<Player> playerList = new ArrayList<Player>();
		playerList.add(play1);
		playerList.add(play2);
		playerList.add(play3);
		playerList.add(play4);

		gameState.setPlayers(playerList);
	}

	@Test
    public void numberOfDice(){
    	
    }
}
