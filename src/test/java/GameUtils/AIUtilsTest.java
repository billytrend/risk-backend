package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class AIUtilsTest {
	private State gameState;
	private Territory[] territories;
	private PlayerInterface[] interfaces;
	private Territory demoland = null;
	private Territory egstate = null;
	private Territory someplace = null;
	private Territory otherplace = null;
	private Player player1, player2, player3;

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
		ArrayList<Player> players = gameState.getPlayers();
		player1 = players.get(0);
		player2 = players.get(1);
		player3 = players.get(2);
		ArmyUtils.givePlayerNArmies(player1, 15);
		ArmyUtils.givePlayerNArmies(player2, 15);
		ArmyUtils.givePlayerNArmies(player3, 15);
	}

	@Test
	public void getStrongestTerritoryTest(){
		HashSet<Territory> territorySet = new HashSet<Territory>();
		
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
	
	@Test
	public void getAllClustersTest(){
		ArmyUtils.deployArmies(player1, egstate, 2);
		ArmyUtils.deployArmies(player1, otherplace, 2);
		ArrayList<HashSet<Territory>> clusters = AIUtils.getAllClusters(gameState, player1);
		
		
		assertEquals(1, clusters.size());
		assertEquals(2, clusters.get(0).size());
		
		ArmyUtils.deployArmies(player2, demoland, 2);
		
		clusters = AIUtils.getAllClusters(gameState, player2);
		assertEquals(1, clusters.size());
		assertEquals(1, clusters.get(0).size());
		
		ArmyUtils.deployArmies(player2, someplace, 2);
		
		clusters = AIUtils.getAllClusters(gameState, player2);

		assertEquals(2, clusters.size());
		assertEquals(1, clusters.get(0).size());
		assertEquals(1, clusters.get(1).size());

	}
	
	@Test
	public void orderClusterTest(){
		ArmyUtils.deployArmies(player1, egstate, 2);
		ArmyUtils.deployArmies(player1, otherplace, 2);
		ArrayList<HashSet<Territory>> clusters = AIUtils.getAllClusters(gameState, player1);
		
		clusters = AIUtils.orderClusters(clusters);
		assertEquals(1, clusters.size());
		assertEquals(2, clusters.get(0).size());
		
		ArmyUtils.deployArmies(player2, demoland, 2);
		ArmyUtils.deployArmies(player2, someplace, 2);
		
		clusters = AIUtils.getAllClusters(gameState, player2);
		clusters = AIUtils.orderClusters(clusters);
	
		assertEquals(2, clusters.size());
		assertEquals(1, clusters.get(0).size());
		assertEquals(1, clusters.get(1).size());
		
		HashSet<Territory> cluster1 = new HashSet<Territory>();
		HashSet<Territory> cluster2 = new HashSet<Territory>();
		HashSet<Territory> cluster3 = new HashSet<Territory>();
		HashSet<Territory> cluster4 = new HashSet<Territory>();
		
		cluster1.add(demoland);
		cluster2.add(egstate);
		cluster3.add(otherplace);
		cluster3.add(someplace);
		cluster4.add(demoland);
		cluster4.add(egstate);
		cluster4.add(otherplace);
		cluster4.add(someplace);
		ArrayList<HashSet<Territory>> testClusters = new ArrayList<HashSet<Territory>>();
		testClusters.add(cluster3);
		testClusters.add(cluster1);
		testClusters.add(cluster4);
		testClusters.add(cluster2);
		
		assertEquals(4, testClusters.size());
		
		testClusters = AIUtils.orderClusters(testClusters);
		
		assertEquals(4, testClusters.size());
		assertEquals(cluster4, testClusters.get(0));
		assertEquals(cluster3, testClusters.get(1));
		assertEquals(1, testClusters.get(2).size());
		assertEquals(1, testClusters.get(3).size());
		assertFalse(testClusters.get(2) ==testClusters.get(3) );
		
	}
	
	@Test
	public void getRandomTerritoryTest(){
		HashSet<Territory> map = new HashSet<Territory>();
		for(Territory t: territories){
			map.add(t);
		}
		Territory random = AIUtils.getRandomTerritory(gameState, map);
		assertTrue(random.equals(egstate) || random.equals(someplace) || random.equals(demoland) || random.equals(otherplace));
		Territory nullTerr = AIUtils.getRandomTerritory(gameState, new HashSet<Territory>());
	}

	@Test
	public void getBiggestThreatTest(){
		
		ArmyUtils.deployArmies(player1, egstate, 1);
		ArmyUtils.deployArmies(player1, otherplace, 3);
		ArmyUtils.deployArmies(player1, demoland, 4);
		ArmyUtils.deployArmies(player1, someplace, 2);
		
		HashSet<Territory> enemyTerritories = TerritoryUtils.getAllEnemyTerritories(gameState, player1);
		
		assertEquals(null, AIUtils.getBiggestThreatToPlayer(gameState, enemyTerritories, player1));
		
		ArmyUtils.destroyArmies(player1, otherplace, 1);
		ArmyUtils.destroyArmies(player1, someplace, 1);
		
		ArmyUtils.deployArmies(player2, otherplace, 3);
		ArmyUtils.deployArmies(player3, someplace, 3);
		
		enemyTerritories = TerritoryUtils.getAllEnemyTerritories(gameState, player1);
		//System.out.println(enemyTerritories.size());
		//assertEquals(otherplace,AIUtils.getBiggestThreatToPlayer(gameState, enemyTerritories, player1));
	}
	@Test
	public void armiesOwnedSurroundingTest(){
		assertEquals(0, AIUtils.armiesOwnedSurroundingTerritory(gameState, egstate, player1));
		ArmyUtils.deployArmies(player1, egstate, 2);
		ArmyUtils.deployArmies(player1, otherplace, 1);
		assertEquals(1, AIUtils.armiesOwnedSurroundingTerritory(gameState, egstate, player1));
		assertEquals(3, AIUtils.armiesOwnedSurroundingTerritory(gameState, demoland, player1));
		
		ArmyUtils.deployArmies(player2, demoland, 4);
		ArmyUtils.deployArmies(player2, someplace, 2);
		
		assertEquals(1, AIUtils.armiesOwnedSurroundingTerritory(gameState, egstate, player1));
		assertEquals(6, AIUtils.armiesOwnedSurroundingTerritory(gameState, egstate, player2));
		assertEquals(3, AIUtils.armiesOwnedSurroundingTerritory(gameState, demoland, player1));
		assertEquals(0, AIUtils.armiesOwnedSurroundingTerritory(gameState, demoland, player2));
	}
	@Test
	public void getTerritoryWithStrongestNeighbourTest(){
		HashSet<Territory> owned = TerritoryUtils.getPlayersTerritories(player1);
		ArmyUtils.deployArmies(player1, egstate, 2);
		ArmyUtils.deployArmies(player1, someplace, 1);
		ArmyUtils.deployArmies(player2, demoland, 3);
		ArmyUtils.deployArmies(player2, otherplace, 2);
		owned = TerritoryUtils.getPlayersTerritories(player1);
		assertEquals(egstate, AIUtils.getTerritoryWithStrongestNeighbour(gameState, owned, player1));
	}
}
