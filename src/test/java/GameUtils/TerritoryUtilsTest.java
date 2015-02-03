package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import GameState.Territory;
import junit.framework.TestCase;
import org.javatuples.Pair;
import java.util.HashSet;
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

    public void testGetAllBorders() throws Exception {
        State st = DemoGameBuilder.buildGame(4, 4);
        HashSet<Pair<Territory, Territory>> terrs = TerritoryUtils.getAllBorders(st);
        assertEquals(terrs.size(), 5);
    }

}