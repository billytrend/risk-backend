
package GameUtils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import GameState.Territory;

public class RuleUtilsTest {
	
	private State gameState;
	private Territory[] territories;

	@Before
	public void stateSetUp(){
		gameState = DemoGameBuilder.buildTestGame(2, 5, 2);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
	}
	
	@Test
	public void doArmyHandoutTest() {
		
	}
	
	@Test
	public void applyFightResultTest(){
		
	}
	
	

}
