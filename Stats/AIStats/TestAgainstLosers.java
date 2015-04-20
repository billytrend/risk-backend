package AIStats;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.Berserker;
import PlayerInput.Boss;
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
	
	public void addLosers(int amount){
		for(int i = 0; i< amount;i++){
			String name = "l" + i;
			Player p = new Player(new TheLoser(gameState), name);
			players.add(p);
		}
	}

	//Border Control@Test
	@Test
    public void testBorderControlwithOneLoser(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addLosers(1);
    }
    public void testBorderControlwithTwoLosers(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addLosers(2);
    }
	@Test
    public void testBorderControlwithThreeLosers(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addLosers(3);
    }
	@Test
    public void testBorderControlwithFourLosers(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addLosers(4);
    }
	@Test
    public void testBorderControlwithFiveLosers(){
		player1 = new Player(new BorderControl(gameState), "BorderControl");
		players.add(player1);
		addLosers(5);
    }
	
	//Communist Defensive
	@Test
	public void testCommunistDefensivewithOneLoser(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addLosers(1);
	}
	@Test
	public void testCommunistDefensivewithTwoLosers(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addLosers(2);
	}
	@Test
	public void testCommunistDefensivewithThreeLosers(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addLosers(3);
	}
	@Test
	public void testCommunistDefensivewithFourLosers(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addLosers(4);
	}
	@Test
	public void testCommunistDefensivewithFiveLosers(){
		player1 = new Player(new CommunistDefensive(gameState), "CommunistDefensive");
		players.add(player1);
		addLosers(5);
	}
	
	
	//Communist Aggressive
	@Test
	public void testCommunistAgressivewithOneLoser() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addLosers(1);
	}
	@Test
	public void testCommunistAgressivewithTwoLosers() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addLosers(2);
	}
	@Test
	public void testCommunistAgressivewithThreeLosers() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addLosers(3);
		
	}
	@Test
	public void testCommunistAgressivewithFourLosers() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addLosers(4);
	}
	@Test
	public void testCommunistAgressivewithFiveLosers() {
		player1 = new Player(new CommunistAggressive(gameState), "CommunistAgressive");
		players.add(player1);
		addLosers(5);
	}
	
	
	//Boss
	@Test
    public void testBosswithOneLoser(){
		player1 = new Player(new Boss(gameState), "Boss");
		players.add(player1);
		addLosers(1);
    }
	@Test
    public void testBosswithTwoLosers(){
		player1 = new Player(new Boss(gameState), "Boss");
		players.add(player1);
		addLosers(2);
    }
	@Test
    public void testBosswithThreeLosers(){
		player1 = new Player(new Boss(gameState), "Boss");
		players.add(player1);
		addLosers(3);
    }
	@Test
    public void testBosswithFourLosers(){
		player1 = new Player(new Boss(gameState), "Boss");
		players.add(player1);
		addLosers(4);
    }
	@Test
    public void testBosswithFiveLosers(){
		player1 = new Player(new Boss(gameState), "Boss");
		players.add(player1);
		addLosers(5);
    }
	
	//Berserker
		@Test
	    public void testBerserkerwithOneLoser(){
			player1 = new Player(new Berserker(gameState), "Berserker");
			players.add(player1);
			addLosers(1);
	    }
		@Test
	    public void testBerserkerwithTwoLosers(){
			player1 = new Player(new Berserker(gameState), "Berserker");
			players.add(player1);
			addLosers(2);
	    }
		@Test
	    public void testBerserkerwithThreeLosers(){
			player1 = new Player(new Berserker(gameState), "Berserker");
			players.add(player1);
			addLosers(3);
	    }
		@Test
	    public void testBerserkerwithFourLosers(){
			player1 = new Player(new Berserker(gameState), "Berserker");
			players.add(player1);
			addLosers(4);
	    }
		@Test
	    public void testBerserkerwithFiveLosers(){
			player1 = new Player(new Berserker(gameState), "Berserker");
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
		for(Player p : players) results += p.getId() + ",";
		results += player1.getNumberOfTurns() + "," +player1.getNumberOfAttacks() + ",";
		results += gameState.getEndPosition(player1);
	    writer.println(results);
		writer.close();
		
	}
	
}
