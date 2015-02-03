package GameUtils;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import GameState.Territory;

public class TerritoryUtilsTest {
	
	private State gameState;
	private Territory[] territories;

	@Before
	public void stateSetUp(){
		gameState = DemoGameBuilder.buildGame(3, 5);
		territories = new Territory[gameState.getTerritories().vertexSet().size()];
		gameState.getTerritories().vertexSet().toArray(territories);
	}
	
	@Test
	public void getAllTerritoriesTest() {
		assertEquals(TerritoryUtils.getAllTerritories(gameState).size(), 4);
		assertTrue(TerritoryUtils.getAllTerritories(gameState).containsAll(Arrays.asList(territories)));
		
		gameState = DemoGameBuilder.buildTestGame(2, 5, 7);
		assertEquals(TerritoryUtils.getAllTerritories(gameState).size(), 7);
	}
	
	@Test
	public void areNeighboursTest() {
		Territory demoland;
		Territory egstate;
		Territory someplace;
		
		for(Territory t : territories){
			String id = t.getId();
			if(id.equals("demoland"))
				demoland = t;
			else if(id.equals("egstate"))
				egstate = t;
			else if(id.equals("someplace"))
				someplace = t;
		}
		
		
	}
	
	@Test
	public void getUnownedTerritoriesTest() {
	}
	
	@Test
	public void getPlayersTerritoriesTest() {
	}
	
	@Test
	public void hasEmptyTerritoriesTest() {
	}
	
	@Test
	public void getNeighboursTest(){
		
	}
	
	@Test
	public void getEnemyNeighboursTest(){
		
	}
	
	@Test
	public void getFriendlyNeighboursTest(){
		
	}
	
	@Test
	public void getTerritoriesWithMoreThanOneArmyTest(){
		
	}
	
	@Test
	public void getPossibleAttackingTerritories(){
		
	}
	
	@Test
	public void getDeployableTest(){
		
	}
	
	@Test
	public void addTerritoryTest(){
		
	}
	
	
	@Test
	public void addBorderTest() {

	}
	
	

}
