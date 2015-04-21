package AIStats;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import PlayerInput.Boss;
import PlayerInput.BorderControl;
import PlayerInput.CommunistAggressive;
import PlayerInput.CommunistDefensive;
import PlayerInput.Nailer;
import PlayerInput.SuperSwapper;
import PlayerInput.TheLoser;

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
	
	public void addLosers(int amount){
		for(int i = 0; i< amount;i++){
			String name = "l" + i;
			Player p = new Player(new TheLoser(gameState), name);
			players.add(p);
		}
	}

//	//Border Control@Test
//	@Test
//    public void testBorderControlwithOneLoser(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addLosers(1);
//    }
//    public void testBorderControlwithTwoLosers(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addLosers(2);
//    }
//	@Test
//    public void testBorderControlwithThreeLosers(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addLosers(3);
//    }
//	@Test
//    public void testBorderControlwithFourLosers(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addLosers(4);
//    }
//	@Test
//    public void testBorderControlwithFiveLosers(){
//		player1 = new Player(new BorderControl(gameState), "BorderControl");
//		players.add(player1);
//		addLosers(5);
//    }
//	
//	//Communist Defensive
//	@Test
//	public void testCommunistDefensivewithOneLoser(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addLosers(1);
//	}
//	@Test
//	public void testCommunistDefensivewithTwoLosers(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addLosers(2);
//	}
//	@Test
//	public void testCommunistDefensivewithThreeLosers(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addLosers(3);
//	}
//	@Test
//	public void testCommunistDefensivewithFourLosers(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addLosers(4);
//	}
//	@Test
//	public void testCommunistDefensivewithFiveLosers(){
//		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
//		players.add(player1);
//		addLosers(5);
//	}
//	
//	
//	//Communist Aggressive
//	@Test
//	public void testCommunistAgressivewithOneLoser() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addLosers(1);
//	}
//	@Test
//	public void testCommunistAgressivewithTwoLosers() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addLosers(2);
//	}
//	@Test
//	public void testCommunistAgressivewithThreeLosers() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addLosers(3);
//		
//	}
//	@Test
//	public void testCommunistAgressivewithFourLosers() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addLosers(4);
//	}
//	@Test
//	public void testCommunistAgressivewithFiveLosers() {
//		player1 = new Player(new CommunistAggressive(gameState), "CommunistAggressive");
//		players.add(player1);
//		addLosers(5);
//	}
//	
//	
//	//Boss
//	@Test
//    public void testBosswithOneLoser(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addLosers(1);
//    }
//	@Test
//    public void testBosswithTwoLosers(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addLosers(2);
//    }
//	@Test
//    public void testBosswithThreeLosers(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addLosers(3);
//    }
//	@Test
//    public void testBosswithFourLosers(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addLosers(4);
//    }
//	@Test
//    public void testBosswithFiveLosers(){
//		player1 = new Player(new Boss(gameState), "Boss");
//		players.add(player1);
//		addLosers(5);
//    }
	//Swapper
			@Test
		    public void testSwapperwithOneLoser(){
				player1 = new Player(new SuperSwapper(gameState), "Swapper");
				players.add(player1);
				addLosers(1);
		    }
			@Test
		    public void testSwapperwithTwoLosers(){
				player1 = new Player(new Nailer(gameState), "Swapper");
				players.add(player1);
				addLosers(2);
		    }
			@Test
		    public void testSwapperwithThreeLosers(){
				player1 = new Player(new Nailer(gameState), "Swapper");
				players.add(player1);
				addLosers(3);
		    }
			@Test
		    public void testSwapperwithFourLosers(){
				player1 = new Player(new Nailer(gameState), "Swapper");
				players.add(player1);
				addLosers(4);
		    }
			@Test
		    public void testSwapperwithFiveLosers(){
				player1 = new Player(new Nailer(gameState), "Swapper");
				players.add(player1);
				addLosers(5);
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
