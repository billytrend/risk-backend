package AIStats;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import PlayerInput.Berserker;
import PlayerInput.Boss;
import PlayerInput.BorderControl;
import PlayerInput.CommunistAggressive;
import PlayerInput.CommunistDefensive;
import PlayerInput.DumbBotInterface;
import PlayerInput.Nailer;
import PlayerInput.SuperSwapper;

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
	final private File stats = new File("Statistics.csv");
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
//	@Test
//    public void testBorderControlwithOneDumbBot(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addDumbBots(1);
//    }
//    public void testBorderControlwithTwoDumbBots(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addDumbBots(2);
//    }
//	@Test
//    public void testBorderControlwithThreeDumbBots(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addDumbBots(3);
//    }
//	@Test
//    public void testBorderControlwithFourDumbBots(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addDumbBots(4);
//    }
//	@Test
//    public void testBorderControlwithFiveDumbBots(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addDumbBots(5);
//    }
//	
//	//Communist Defensive
//	@Test
//	public void testCommunistDefensivewithOneDumbBot(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addDumbBots(1);
//	}
//	@Test
//	public void testCommunistDefensivewithTwoDumbBots(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addDumbBots(2);
//	}
//	@Test
//	public void testCommunistDefensivewithThreeDumbBots(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addDumbBots(3);
//	}
//	@Test
//	public void testCommunistDefensivewithFourDumbBots(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addDumbBots(4);
//	}
//	@Test
//	public void testCommunistDefensivewithFiveDumbBots(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addDumbBots(5);
//	}
//	
//	
//	//Communist Aggressive
//	@Test
//	public void testCommunistAgressivewithOneDumbBot() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addDumbBots(1);
//	}
//	@Test
//	public void testCommunistAgressivewithTwoDumbBots() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addDumbBots(2);
//	}
//	@Test
//	public void testCommunistAgressivewithThreeDumbBots() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addDumbBots(3);
//		
//	}
//	@Test
//	public void testCommunistAgressivewithFourDumbBots() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addDumbBots(4);
//	}
//	@Test
//	public void testCommunistAgressivewithFiveDumbBots() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addDumbBots(5);
//	}
//	
//	
//	//Boss
//	@Test
//    public void testBosswithOneDumbBot(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addDumbBots(1);
//    }
//	@Test
//    public void testBosswithTwoDumbBots(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addDumbBots(2);
//    }
//	@Test
//    public void testBosswithThreeDumbBots(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addDumbBots(3);
//    }
//	@Test
//    public void testBosswithFourDumbBots(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addDumbBots(4);
//    }
//	@Test
//    public void testBosswithFiveDumbBots(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addDumbBots(5);
//    }
	//SuperSwapper
	@Test
    public void testSwapperwithOneDumbBot(){
		player1 = new Player(new SuperSwapper(gameState), "Swapper");
		players.add(player1);
		addDumbBots(1);
    }
	@Test
    public void testSwapperwithTwoDumbBots(){
		player1 = new Player(new SuperSwapper(gameState), "Swapper");
		players.add(player1);
		addDumbBots(2);
    }
	@Test
    public void testSwapperwithThreeDumbBots(){
		player1 = new Player(new SuperSwapper(gameState), "Swapper");
		players.add(player1);
		addDumbBots(3);
    }
	@Test
    public void testSwapperwithFourDumbBots(){
		player1 = new Player(new SuperSwapper(gameState), "Swapper");
		players.add(player1);
		addDumbBots(4);
    }
	@Test
    public void testSwapperwithFiveDumbBots(){
		player1 = new Player(new Nailer(gameState), "Swapper");
		players.add(player1);
		addDumbBots(5);
    }


	@After
	public void runGameAndWriteStats() throws InterruptedException{
		gameState.setPlayers(players);
		
		GameEngine gameThr = new GameEngine(gameState);
	    Thread gameThread = new Thread(gameThr);
	    gameThread.start();
	    gameThread.join();
	    gameState.print();
		String results = "";
		for(int i = 0; i<6; i++){
			if(players.size() > i) results += players.get(i).getId() + ",";
			else results += ",";
		}
		results += player1.getNumberOfTurns() + "," +player1.getNumberOfAttacks() + ",";
		results += gameState.getEndPosition(player1);
	    writer.println(results);
		writer.close();
		
	}
	
}
