package AIStats;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import PlayerInput.Berserker;
import PlayerInput.Billy;
import PlayerInput.BorderControl;
import PlayerInput.CommunistAggressive;
import PlayerInput.CommunistDefensive;
import PlayerInput.DumbBotInterface;

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

public class TestAgainstDumbBot {
	private Player player1;
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
	
	public void addDumbBots(int amount){
		for(int i = 0; i< amount;i++){
			String name = "d" + i;
			Player p = new Player(new DumbBotInterface(gameState), name);
			players.add(p);
		}
	}

	//Border Control@Test
	@Test
    public void testBorderControlwithOneDumbBot(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addDumbBots(1);
    }
    public void testBorderControlwithTwoDumbBots(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addDumbBots(2);
    }
	@Test
    public void testBorderControlwithThreeDumbBots(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addDumbBots(3);
    }
	@Test
    public void testBorderControlwithFourDumbBots(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addDumbBots(4);
    }
	@Test
    public void testBorderControlwithFiveDumbBots(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addDumbBots(5);
    }
	
	//Communist Defensive
	@Test
	public void testCommunistDefensivewithOneDumbBot(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addDumbBots(1);
	}
	@Test
	public void testCommunistDefensivewithTwoDumbBots(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addDumbBots(2);
	}
	@Test
	public void testCommunistDefensivewithThreeDumbBots(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addDumbBots(3);
	}
	@Test
	public void testCommunistDefensivewithFourDumbBots(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addDumbBots(4);
	}
	@Test
	public void testCommunistDefensivewithFiveDumbBots(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addDumbBots(5);
	}
	
	
	//Communist Aggressive
	@Test
	public void testCommunistAgressivewithOneDumbBot() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addDumbBots(1);
	}
	@Test
	public void testCommunistAgressivewithTwoDumbBots() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addDumbBots(2);
	}
	@Test
	public void testCommunistAgressivewithThreeDumbBots() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addDumbBots(3);
		
	}
	@Test
	public void testCommunistAgressivewithFourDumbBots() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addDumbBots(4);
	}
	@Test
	public void testCommunistAgressivewithFiveDumbBots() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addDumbBots(5);
	}
	
	
	//Billy
	@Test
    public void testBillywithOneDumbBot(){
		player1 = new Player(new Billy(gameState), "Billy");
		players.add(player1);
		addDumbBots(1);
    }
	@Test
    public void testBillywithTwoDumbBots(){
		player1 = new Player(new Billy(gameState), "Billy");
		players.add(player1);
		addDumbBots(2);
    }
	@Test
    public void testBillywithThreeDumbBots(){
		player1 = new Player(new Billy(gameState), "Billy");
		players.add(player1);
		addDumbBots(3);
    }
	@Test
    public void testBillywithFourDumbBots(){
		player1 = new Player(new Billy(gameState), "Billy");
		players.add(player1);
		addDumbBots(4);
    }
	@Test
    public void testBillywithFiveDumbBots(){
		player1 = new Player(new Billy(gameState), "Billy");
		players.add(player1);
		addDumbBots(5);
    }
	
//	//Berserker
//		@Test
//	    public void testBerserkerwithOneDumbBot(){
//			player1 = new Player(new Berserker(gameState), "Berserker");
//			players.add(player1);
//			addDumbBots(1);
//	    }
//		@Test
//	    public void testBerserkerwithTwoDumbBots(){
//			player1 = new Player(new Berserker(gameState), "Berserker");
//			players.add(player1);
//			addDumbBots(2);
//	    }
//		@Test
//	    public void testBerserkerwithThreeDumbBots(){
//			player1 = new Player(new Berserker(gameState), "Berserker");
//			players.add(player1);
//			addDumbBots(3);
//	    }
//		@Test
//	    public void testBerserkerwithFourDumbBots(){
//			player1 = new Player(new Berserker(gameState), "Berserker");
//			players.add(player1);
//			addDumbBots(4);
//	    }
//		@Test
//	    public void testBerserkerwithFiveDumbBots(){
//			player1 = new Player(new Berserker(gameState), "Berserker");
//			players.add(player1);
//			addDumbBots(5);
//	    }
	@After
	public void runGameAndWriteStats() throws InterruptedException{
		gameState.setPlayers(players);
		
		GameEngine gameThr = new GameEngine(gameState);
	    Thread gameThread = new Thread(gameThr);
	    Timer t = new Timer(gameThread.getName());
	    gameThread.start();
	    gameThread.join();
	    gameState.print();
		String results = "";
		for(Player p : players) results += p.getId() + ",";
		results += player1.getNumberOfTurns() + "," +player1.getNumberOfAttacks() + ",";
		results += gameState.getEndPosition(player1);
	    writer.println(results);
		writer.close();
		
	}
	
}
