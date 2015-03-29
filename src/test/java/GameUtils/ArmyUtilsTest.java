package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import com.esotericsoftware.minlog.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ArmyUtilsTest {
	
	private State gameState;
	private Territory[] territories;

	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		Log.NONE();
		gameState = DemoGameBuilder.buildTestGame(2, 5, 2, interfaces);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
	}
	

	
	@Test
	public void getUndeployedArmiesTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 5);
		
		// objects should be the same in both arrays
		assertArrayEquals(ArmyUtils.getUndeployedArmies(player1).toArray(),
				player1.getArmies().toArray());
		
		assertEquals(ArmyUtils.getUndeployedArmies(player2).size(), 5);
		assertArrayEquals(ArmyUtils.getUndeployedArmies(player2).toArray(), 
				player2.getArmies().toArray());
		
		ArmyUtils.deployArmies(player1, territories[0], 2);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 3);
		assertFalse(ArmyUtils.getUndeployedArmies(player1).equals(player1.getArmies()));
	}
	
	@Test
	public void somePlayerHasUndeployedArmies(){
		ArrayList<Player> players = gameState.getPlayers();
		
		assertTrue(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
		

		ArmyUtils.deployArmies(players.get(0), territories[0], 5);
		assertTrue(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
		
		ArmyUtils.deployArmies(players.get(1), territories[1], 5);
		assertFalse(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
	}
	
	@Test
    public void givePlayerNArmiesTest() {
		Player player1 = gameState.getPlayers().get(0);
		assertEquals(player1.getArmies().size(), 5);
		
		ArmyUtils.givePlayerNArmies(player1, 3);
		assertEquals(player1.getArmies().size(), 8);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 8);
    }
	
	@Test 
	public void getArmiesOnTerritory(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		Territory territory1 = territories[0];
		Territory territory2 = territories[1];
		
		ArmyUtils.deployArmies(player1, territory1, 1);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, territory1).size(), 1);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, territory1).get(0),
				player1.getArmies().get(0));
		
		ArmyUtils.deployArmies(player1, territory1, 1);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, territory1).size(), 2);
		assertTrue(ArmyUtils.getArmiesOnTerritory(player1, territory1)
				.contains(player1.getArmies().get(0)));
		assertTrue(ArmyUtils.getArmiesOnTerritory(player1, territory1)
				.contains(player1.getArmies().get(1)));
		
		ArmyUtils.deployArmies(player2, territory2, 5);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player2, territory2).size(), 5);
		assertArrayEquals(ArmyUtils.getArmiesOnTerritory(player2, territory2).toArray(),
				player2.getArmies().toArray());
	}
	
	@Test 
	public void getNumberOfArmiesOnTerritory(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		Territory territory1 = territories[0];
		Territory territory2 = territories[1];
		
		ArmyUtils.deployArmies(player1, territory1, 2);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory1), 2);

		ArmyUtils.deployArmies(player2, territory2, 4);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, territory2), 4);
	}
	
	@Test
    public void destroyArmiesTest() {
    	Player player1 = gameState.getPlayers().get(0);
		Territory territory1 = territories[0];
		
		ArmyUtils.deployArmies(player1, territory1, 2);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory1), 2);
		assertEquals(player1.getArmies().size(), 5);
		
		ArmyUtils.destroyArmies(player1, territory1, 1);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory1), 1);
		assertEquals(player1.getArmies().size(), 4);
    }
	
	@Test
	public void moveArmies(){
		Player player1 = gameState.getPlayers().get(0);
		Territory territory1 = territories[0];
		Territory territory2 = territories[1];
		
		ArmyUtils.deployArmies(player1, territory1, 3);
		ArmyUtils.deployArmies(player1, territory2, 2);
		
		ArrayList<Army> armiesOnTerritory1 = ArmyUtils.getArmiesOnTerritory(player1, territory1);
		assertEquals(armiesOnTerritory1.size(), 3);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory2), 2);
		
		ArmyUtils.moveArmies(player1, territory1, territory2, 2);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory1), 1);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory2), 4);
		
		ArrayList<Army> movedArmies = armiesOnTerritory1;
		movedArmies.removeAll(ArmyUtils.getArmiesOnTerritory(player1, territory1));
		assertTrue(ArmyUtils.getArmiesOnTerritory(player1, territory2).containsAll(movedArmies));	
	}

	@Test
	public void deployArmies(){
		Player player1 = gameState.getPlayers().get(0);
		Territory territory1 = territories[0];
		
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 0);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 5);
		
		ArmyUtils.deployArmies(player1, territory1, 3);
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertTrue(TerritoryUtils.getPlayersTerritories(player1).contains(territory1));
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 2);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territory1), 3);
		
		ArrayList<Army> deployedAndUndeployed = ArmyUtils.getArmiesOnTerritory(player1, territory1);
		deployedAndUndeployed.addAll(ArmyUtils.getUndeployedArmies(player1));
		
		// all objects are correct
		assertArrayEquals(deployedAndUndeployed.toArray(), player1.getArmies().toArray());
	}
	
	@Test
	public void getNumberOfMoveableArmies(){
		Player player1 = gameState.getPlayers().get(0);
		Territory territory1 = territories[0];
		
		ArmyUtils.deployArmies(player1, territory1, 1);
		assertEquals(ArmyUtils.getNumberOfMoveableArmies(player1, territory1), 0);
		
		ArmyUtils.deployArmies(player1, territory1, 1);
		assertEquals(ArmyUtils.getNumberOfMoveableArmies(player1, territory1), 1);
	}
	
}
