package GameUtils;

import static org.junit.Assert.*;

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
		gameState = DemoGameBuilder.buildTestGame(2, 5, 2);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
	}
	
	@Test
	public void getAllTerritoriesTest() {
	}
	
	@Test
	public void areNeighboursTest() {
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
	public void addBorderTest(){
		
	}
	
	
	@Test
	public void countTerritoriesTest(){
		
	}
	
	
	

}
