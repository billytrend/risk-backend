package GameUtils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

public class PlayerUtilsTest {

	private State gameState;
	private Territory[] territories;
	
	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildTestGame(2, 2, interfaces);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
		GameEngine engine = new GameEngine(gameState);
	}
	
	@Test
	public void playerContinentsTest() {
	}
	
	@Test
	public void getNumberOfTerritoriesOwnedTest(){
		Player player1 = gameState.getPlayers().get(0);
		ArmyUtils.givePlayerNArmies(player1, 5);
		
		Territory territory1 = territories[0];
		Territory territory2 = territories[1];
		
		assertEquals(PlayerUtils.getNumberOfTerritoriesOwned(player1), 0);
		
		ArmyUtils.deployArmies(player1, territory1, 2);
		assertEquals(PlayerUtils.getNumberOfTerritoriesOwned(player1), 1);
		
		ArmyUtils.deployArmies(player1, territory2, 2);
		assertEquals(PlayerUtils.getNumberOfTerritoriesOwned(player1), 2);
	}
	
	@Test
	public void getTerritoryOwnerTest(){
		Player player1 = gameState.getPlayers().get(0);
		ArmyUtils.givePlayerNArmies(player1, 5);
		Territory territory1 = territories[0];
		
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
		
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		
		Territory territory1 = territories[0];
		
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
