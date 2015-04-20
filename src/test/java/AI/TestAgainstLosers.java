package AI;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.Billy;
import PlayerInput.BorderControl;
import PlayerInput.CommunistAggressive;
import PlayerInput.CommunistDefensive;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import PlayerInput.TheLoser;

import com.esotericsoftware.minlog.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;

public class TestAgainstLosers {
	Player player1, player2, player3, player4;
	State gameState;
	final private File stats = new File("Statistics");
	PrintWriter writer;
	@Before
	public void setup() throws InterruptedException, IOException {
		
		writer = new PrintWriter(new BufferedWriter (new FileWriter(stats, true)));
		
		PlayerInterface[] interfaces = new PlayerInterface[] {
				new DumbBotInterface(), new DumbBotInterface(),
				new DumbBotInterface(), new DumbBotInterface() };

		gameState = RiskMapGameBuilder.buildGame(interfaces);

		
	}

	@Test
    public void testBorderControl(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
    }
	@Test
	public void testCommunistAgressive() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
	}
	
	@Test
	public void testCommunistDefensive(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
	}
	@Test
    public void testBilly(){
		player1 = new Player(new Billy(gameState), "Billy");
    }
	@After
	public void runGameAndWriteStats() throws InterruptedException{
		player2 = new Player(new TheLoser(gameState));
		player3 = new Player(new TheLoser(gameState));
		player4 = new Player(new TheLoser(gameState));
		
		ArrayList<Player> playerList = new ArrayList<Player>();
		playerList.add(player1);
		playerList.add(player2);
		playerList.add(player3);
		playerList.add(player4);
		
		gameState.setPlayers(playerList);
		
		
		GameEngine gameThr = new GameEngine(gameState);
	    Thread gameThread = new Thread(gameThr);
	    Timer t = new Timer(gameThread.getName());
	    gameThread.start();
	    gameThread.join();
	    gameState.print();
		String results = player1.getId() + ",";
		results += player1.getNumberOfTurns() + "," +player1.getNumberOfAttacks() + ",";
	    Player winner = gameState.getWinner();
	    int win = -1;
	    if(winner == player1) win = 1;
	    results += win;
	    writer.println(results);
		writer.close();
		
	}
	
}
