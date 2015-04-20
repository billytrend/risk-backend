package AIStats;

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
	private Player player1, player2, player3, player4, player5, player6;
	private State gameState;
	final private File stats = new File("Statistics");
	private PrintWriter writer;
	private ArrayList<Player> players;
	@Before
	public void setup() throws InterruptedException, IOException {
		writer = new PrintWriter(new BufferedWriter (new FileWriter(stats, true)));
		gameState = RiskMapGameBuilder.buildGame(null);
	players = new ArrayList<Player>();
	}

	@Test
    public void testBorderControlwithOneLoser(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		player2 = new Player(new TheLoser(gameState));
		players.add(player1);
		players.add(player2);
    }
	@Test
	public void testCommunistAgressivewithOneLoser() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		player2 = new Player(new TheLoser(gameState));
		players.add(player1);
		players.add(player2);
	}
	
	@Test
	public void testCommunistDefensivewithOneLoser(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		player2 = new Player(new TheLoser(gameState));
		players.add(player1);
		players.add(player2);
	}
	@Test
    public void testBillywithOneLoser(){
		player1 = new Player(new Billy(gameState), "Billy");
		player2 = new Player(new TheLoser(gameState));
		players.add(player1);
		players.add(player2);
    }
	@After
	public void runGameAndWriteStats() throws InterruptedException{

		
		gameState.setPlayers(players);
		
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
