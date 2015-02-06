package GameUtils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameState.Continent;
import GameState.State;
import GameState.Territory;

public class ContinentUtilsTest {
	
	private State gameState;
	private Territory[] territories;
	
	@Before
	public void stateSetUp(){
		gameState = DemoGameBuilder.buildGame(3, 10);
		territories = new Territory[gameState.getTerritories().vertexSet().size()];
		gameState.getTerritories().vertexSet().toArray(territories);
	}
	
	@Test
	public void getContinentsTest() {
		ArrayList<Continent> continents = gameState.getContinents();
		assertEquals(continents.size(), 2);
		Continent contAB = continents.get(0);
		assertEquals(contAB.getId(), "demoContAB");
		assertEquals(contAB.getTerritories().size(), 2);
		assertEquals(contAB.getArmyReward(), 4);
		
		
	
	}
	

}
