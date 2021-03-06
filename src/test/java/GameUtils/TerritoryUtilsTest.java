package GameUtils;


import GameBuilders.DemoGameBuilder;

import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;



public class TerritoryUtilsTest {

	private State gameState;
	private Territory[] territories;
	private PlayerInterface[] interfaces;
	private Territory demoland = null;
	private Territory egstate = null;
	private Territory someplace = null;
	private Territory otherplace = null;

	@Before
	public void stateSetUp(){
		interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildGame(interfaces);
		territories = new Territory[gameState.getTerritories().vertexSet().size()];
		gameState.getTerritories().vertexSet().toArray(territories);
		GameEngine engine = new GameEngine(gameState);
		
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
	public void getAllTerritoriesTest() {
		assertEquals(TerritoryUtils.getAllTerritories(gameState).size(), 4);
		assertTrue(TerritoryUtils.getAllTerritories(gameState).containsAll(Arrays.asList(territories)));
		
		gameState = DemoGameBuilder.buildTestGame(3, 7, interfaces);
		assertEquals(TerritoryUtils.getAllTerritories(gameState).size(), 7);
	}
	
	@Test
	public void areNeighboursTest() {
		
		assertTrue(TerritoryUtils.areNeighbours(gameState, demoland, egstate));
		assertTrue(TerritoryUtils.areNeighbours(gameState, egstate, someplace));
		assertFalse(TerritoryUtils.areNeighbours(gameState, demoland, someplace));		
	}

	@Test
	public void testGetAllBorders() throws Exception {
		HashSet<Pair<Territory, Territory>> terrs = TerritoryUtils.getAllBorders(gameState);
		assertEquals(terrs.size(), 5);
	}

	@Test
	public void getUnownedTerritoriesTest() {
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		ArmyUtils.deployArmies(player1, territories[0], 1);
		ArmyUtils.deployArmies(player1, territories[1], 1);
		
		HashSet<Territory> unownedTerritories = TerritoryUtils.getUnownedTerritories(gameState);
		
		assertEquals(unownedTerritories.size(), 2);
		assertTrue(unownedTerritories.contains(territories[2]));
		assertTrue(unownedTerritories.contains(territories[3]));
		
		ArmyUtils.deployArmies(player2, territories[2], 1);
		
		unownedTerritories = TerritoryUtils.getUnownedTerritories(gameState);
		
		assertEquals(unownedTerritories.size(), 1);
		assertTrue(unownedTerritories.contains(territories[3]));
	}
	
	
	@Test
	public void getPlayersTerritoriesTest() {
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		
		ArmyUtils.deployArmies(player1, territories[0], 1);
		ArmyUtils.deployArmies(player1, territories[1], 1);
		ArmyUtils.deployArmies(player2, territories[2], 1);
		
		HashSet<Territory> player1Territories = TerritoryUtils.getPlayersTerritories(player1);
		HashSet<Territory> player2Territories = TerritoryUtils.getPlayersTerritories(player2);
		
		assertEquals(player1Territories.size(), 2);
		assertEquals(player2Territories.size(), 1);
		assertTrue(player1Territories.contains(territories[0]));
		assertTrue(player1Territories.contains(territories[1]));
		assertTrue(player2Territories.contains(territories[2]));
	}
	
	@Test
	public void hasEmptyTerritoriesTest() {
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		
		ArmyUtils.deployArmies(player1, territories[0], 1);
		ArmyUtils.deployArmies(player1, territories[1], 1);
		
		assertTrue(TerritoryUtils.hasEmptyTerritories(gameState));
		
		ArmyUtils.deployArmies(player2, territories[2], 1);
		ArmyUtils.deployArmies(player2, territories[3], 1);
		
		assertFalse(TerritoryUtils.hasEmptyTerritories(gameState));
	}
	
	@Test
	public void getNeighboursTest(){
		
		HashSet<Territory> neighboursDemoland = TerritoryUtils.getNeighbours(gameState, demoland);
		HashSet<Territory> neighboursEgstate = TerritoryUtils.getNeighbours(gameState, egstate);
		
		assertEquals(neighboursDemoland.size(), 2);
		assertTrue(neighboursDemoland.contains(egstate));
		assertTrue(neighboursDemoland.contains(otherplace));
		
		assertEquals(neighboursEgstate.size(), 3);
		assertTrue(neighboursEgstate.contains(demoland));
		assertTrue(neighboursEgstate.contains(otherplace));
		assertTrue(neighboursEgstate.contains(someplace));
	}
	
	@Test
	public void getEnemyNeighboursTest(){
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, someplace, 1);
		ArmyUtils.deployArmies(player2, demoland, 1);
		ArmyUtils.deployArmies(player2, otherplace, 1);
		
		HashSet<Territory> enemyNeighboutsEgstate = TerritoryUtils.getEnemyNeighbours(gameState, egstate, player1);
		
		assertEquals(enemyNeighboutsEgstate.size(), 2);
		assertTrue(enemyNeighboutsEgstate.contains(demoland));
		assertTrue(enemyNeighboutsEgstate.contains(otherplace));
	}
	
	@Test
	public void getFriendlyNeighboursTest(){
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, someplace, 1);
		ArmyUtils.deployArmies(player2, demoland, 1);
		ArmyUtils.deployArmies(player2, otherplace, 1);
		
		HashSet<Territory> friendlyNeighboutsEgstate = TerritoryUtils.getFriendlyNeighbours(gameState, egstate, player1);
		
		assertEquals(friendlyNeighboutsEgstate.size(), 1);
		assertTrue(friendlyNeighboutsEgstate.contains(someplace));
		assertFalse(friendlyNeighboutsEgstate.contains(demoland));
		assertFalse(friendlyNeighboutsEgstate.contains(otherplace));
	}
	
	@Test
	public void getTerritoriesWithMoreThanOneArmyTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		
		ArmyUtils.givePlayerNArmies(player1, 15);
		
		ArmyUtils.deployArmies(player1, territories[0], 2);
		ArmyUtils.deployArmies(player1, territories[1], 3);
		ArmyUtils.deployArmies(player1, territories[2], 1);
		ArmyUtils.deployArmies(player1, territories[3], 1);
	
		HashSet<Territory> moreThanOneArmyTerritories = TerritoryUtils.getTerritoriesWithMoreThanOneArmy(player1);
		assertEquals(moreThanOneArmyTerritories.size(), 2);
		assertTrue(moreThanOneArmyTerritories.contains(territories[0]));
		assertTrue(moreThanOneArmyTerritories.contains(territories[1]));
	}
	
	@Test
	public void getPossibleAttackingTerritories(){
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		ArmyUtils.givePlayerNArmies(player1, 15);
		ArmyUtils.givePlayerNArmies(player2, 15);
		
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, otherplace, 3);
		ArmyUtils.deployArmies(player1, demoland, 3);
		ArmyUtils.deployArmies(player2, someplace, 1);
		
		HashSet<Territory> possibleAttackingPlayer1 = TerritoryUtils.getPossibleAttackingTerritories(gameState, player1);
		HashSet<Territory> possibleAttackingPlayer2 = TerritoryUtils.getPossibleAttackingTerritories(gameState, player2);
		
		assertEquals(possibleAttackingPlayer1.size(), 1);
		assertEquals(possibleAttackingPlayer2.size(), 0);
		assertTrue(possibleAttackingPlayer1.contains(otherplace));
	}
	
	@Test
	public void getDeployableTest(){
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 15);
		ArmyUtils.givePlayerNArmies(player2, 15);
		
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, otherplace, 3);
		ArmyUtils.deployArmies(player2, demoland, 3);
		ArmyUtils.deployArmies(player2, someplace, 3);
		
		HashSet<Territory> deployablePlayer1 = TerritoryUtils.getDeployable(gameState, player1);
		HashSet<Territory> deployablePlayer2 = TerritoryUtils.getDeployable(gameState, player2);
		
		assertEquals(deployablePlayer1.size(), 1);
		assertEquals(deployablePlayer2.size(), 0);
		
	}
	
	@Test
	public void addTerritoryTest(){
		assertEquals(TerritoryUtils.getAllTerritories(gameState).size(), 4);
		TerritoryUtils.addTerritory(gameState, new Territory("nice", 5));
		assertEquals(TerritoryUtils.getAllTerritories(gameState).size(), 5);
	}
	
	
	@Test
	public void addBorderTest() {
	
		
		HashSet<Territory> neighboursDemoland = TerritoryUtils.getNeighbours(gameState, demoland);
		assertEquals(neighboursDemoland.size(), 2);
		
		TerritoryUtils.addBorder(gameState, demoland, someplace);
		neighboursDemoland = TerritoryUtils.getNeighbours(gameState, demoland);
		assertEquals(neighboursDemoland.size(), 3);
	}
	
	@Test
	public void getTerritoryByNameTest(){
		assertEquals(TerritoryUtils.getTerritoryByName(gameState, "egstate"), egstate);
		assertEquals(TerritoryUtils.getTerritoryByName(gameState, "nonterritory"), null);
	}
	
	@Test
	public void getAllCountryNamesTest(){
		ArrayList<String> countryNames = TerritoryUtils.getAllCountryNames(gameState);
		assertEquals(4, countryNames.size());
		assertTrue(countryNames.contains("demoland"));
		assertTrue(countryNames.contains("egstate"));
		assertTrue(countryNames.contains("otherplace"));
		assertTrue(countryNames.contains("someplace"));
	}
	
	@Test
	public void getAllBorderPairsTest(){
		ArrayList<String[]> pairs = TerritoryUtils.getAllBorderPairs(gameState);
		assertEquals(5, pairs.size());
	}
	
	
}