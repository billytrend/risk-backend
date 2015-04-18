package GameUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

public class AIUtilsTest {
	private State gameState;
	private Territory[] territories;
	PlayerInterface[] interfaces;
	Territory demoland = null;
	Territory egstate = null;
	Territory someplace = null;
	Territory otherplace = null;

	@Before
	public void stateSetUp(){
		interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildGame(interfaces);
		territories = new Territory[gameState.getTerritories().vertexSet().size()];
		gameState.getTerritories().vertexSet().toArray(territories);
		
		for(Territory t : territories){
			String id = t.getId();
			if(id.equals("demoland"))
				demoland = t;
			if(id.equals("egstate"))
				egstate = t;
			if(id.equals("someplace"))
				someplace = t;
			if(id.equals("otherplace"))
				otherplace = t;
		}
	}

	@Test
	public void getStrongestTerritoryTest(){
		HashSet<Territory> territorySet = new HashSet<Territory>();
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		Player player3 = players.get(2);
		ArmyUtils.givePlayerNArmies(player1, 10);
		ArmyUtils.givePlayerNArmies(player2, 10);
		ArmyUtils.givePlayerNArmies(player3, 10);
		
		assertEquals(null, AIUtils.getStrongestTerritory(gameState, territorySet));
		territorySet = TerritoryUtils.getAllTerritories(gameState);
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, otherplace, 3);
		ArmyUtils.deployArmies(player1, demoland, 4);
		ArmyUtils.deployArmies(player1, someplace, 2);
		
		assertEquals(demoland, AIUtils.getStrongestTerritory(gameState, territorySet));
		
		ArmyUtils.destroyArmies(player1, egstate, 1);
		ArmyUtils.destroyArmies(player1, otherplace, 1);
		ArmyUtils.destroyArmies(player1, someplace, 1);
		
		ArmyUtils.deployArmies(player2, egstate, 1);
		ArmyUtils.deployArmies(player2, otherplace, 5);
		ArmyUtils.deployArmies(player3, someplace, 3);
		
		HashSet<Territory> enemyTerritories = new HashSet<Territory>();
		enemyTerritories.add(egstate);
		enemyTerritories.add(otherplace);
		enemyTerritories.add(someplace);
		assertEquals(otherplace,AIUtils.getStrongestTerritory(gameState, enemyTerritories));
		
		
		
		
		
	}
	@Test
	public void getWeakestTerritoryTest(){
		HashSet<Territory> territorySet = new HashSet<Territory>();
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		Player player3 = players.get(2);
		ArmyUtils.givePlayerNArmies(player1, 15);
		ArmyUtils.givePlayerNArmies(player2, 15);
		ArmyUtils.givePlayerNArmies(player3, 15);
		
		assertEquals(AIUtils.getWeakestTerritory(gameState, territorySet),null);
		territorySet = TerritoryUtils.getAllTerritories(gameState);
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, otherplace, 3);
		ArmyUtils.deployArmies(player1, demoland, 4);
		ArmyUtils.deployArmies(player1, someplace, 2);
		
		assertEquals(AIUtils.getWeakestTerritory(gameState, territorySet), egstate);
		
		ArmyUtils.destroyArmies(player1, egstate, 1);
		ArmyUtils.destroyArmies(player1, otherplace, 3);
		ArmyUtils.destroyArmies(player1, someplace, 2);
		
		ArmyUtils.deployArmies(player2, egstate, 5);
		ArmyUtils.deployArmies(player2, otherplace, 1);
		ArmyUtils.deployArmies(player3, someplace, 3);
		
		HashSet<Territory> enemyTerritories = new HashSet<Territory>();
		enemyTerritories.add(egstate);
		enemyTerritories.add(otherplace);
		enemyTerritories.add(someplace);
		assertEquals(otherplace,AIUtils.getWeakestTerritory(gameState, enemyTerritories));
		
		
	}
	
	@Test
	public void goodIdeaTest(){
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 15);
		ArmyUtils.givePlayerNArmies(player2, 15);
		
		ArmyUtils.deployArmies(player2, egstate, 2);
		ArmyUtils.deployArmies(player1, otherplace, 5);
		ArmyUtils.deployArmies(player2, demoland, 3);
		ArmyUtils.deployArmies(player1, someplace, 2);
		
		assertTrue(AIUtils.goodIdea(gameState, otherplace, demoland, 1));
		assertFalse(AIUtils.goodIdea(gameState, someplace, demoland, 1));
		assertFalse(AIUtils.goodIdea(gameState, someplace, egstate, 1));
		assertTrue(AIUtils.goodIdea(gameState,  otherplace,demoland,1.25));
		assertFalse(AIUtils.goodIdea(gameState, someplace, otherplace, 1.25));
	}
	
	
}
