package GameUtils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import GameState.Player;
import GameState.State;
import GameState.Territory;

public class PlayerUtilsTest {

	private State gameState;
	private ArrayList<Territory> territories;
	
	@Before
	public void stateSetUp(){
		
        // creating players
        ArrayList<Player> ps = new ArrayList<Player>();
        // players dont need to have interfaces
        ps.add(new Player(null, 5, 1));
        ps.add(new Player(null, 4, 2));
        
        gameState = new State(ps);
        territories = new ArrayList<Territory>();
        
        Territory demoLandA = new Territory("land1");
        Territory demoLandB = new Territory("land2");
     
        territories.add(demoLandA);
        territories.add(demoLandB);
        
        TerritoryUtils.addTerritory(gameState, demoLandA);
        TerritoryUtils.addTerritory(gameState, demoLandB);
        TerritoryUtils.addBorder(gameState, demoLandA, demoLandB);
	}
	
	@Test
	public void playerContinentsTest() {
	}
	
	@Test
	public void getNumberOfTerritoriesOwnedTest(){
		Player player1 = gameState.getPlayers().get(0);
		Territory territory1 = territories.get(0);
		Territory territory2 = territories.get(1);
		
		assertEquals(PlayerUtils.getNumberOfTerritoriesOwned(player1), 0);
		
		ArmyUtils.deployArmies(player1, territory1, 2);
		assertEquals(PlayerUtils.getNumberOfTerritoriesOwned(player1), 1);
		
		ArmyUtils.deployArmies(player1, territory2, 2);
		assertEquals(PlayerUtils.getNumberOfTerritoriesOwned(player1), 2);
	}
	
	@Test
	public void getTerritoryOwnerTest(){
		Player player1 = gameState.getPlayers().get(0);
		Territory territory1 = territories.get(0);
		
		assertEquals(PlayerUtils.getTerritoryOwner(gameState, territory1), null);
		ArmyUtils.deployArmies(player1, territory1, 2);
		
		assertEquals(PlayerUtils.getTerritoryOwner(gameState, territory1), player1);
	}
	
	
	@Test
	public void countPlayersTest(){
		assertEquals(PlayerUtils.countPlayers(gameState), 2);
	}
	
	@Test
	public void playerIsOutTest(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		Territory territory1 = territories.get(0);
		
		ArmyUtils.deployArmies(player1, territory1, 2);
		assertTrue(PlayerUtils.playerIsOut(player2));
		assertFalse(PlayerUtils.playerIsOut(player1));
	}
	
	@Test
	public void removePlayerTest(){
		Player player1 = gameState.getPlayers().get(0);
		assertEquals(PlayerUtils.countPlayers(gameState), 2);
		PlayerUtils.removePlayer(gameState, player1);
		assertEquals(PlayerUtils.countPlayers(gameState), 1);
		
		// the array of players in the gameState should be unchanged
		// since it holds all players that participate in the game
		// even the ones that have already lost
		assertEquals(gameState.getPlayers().size(), 2);
	}

}
