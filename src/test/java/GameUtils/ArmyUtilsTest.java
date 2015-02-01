package GameUtils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import GameState.*;
import PlayerInput.DumbBotInterface;

public class ArmyUtilsTest {
	
	State gameState;
	ArrayList<Territory> territories;

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
        Territory demoLandC = new Territory("land3");
        Territory demoLandD = new Territory("land4");
        
        territories.add(demoLandA);
        territories.add(demoLandB);
        territories.add(demoLandC);
        territories.add(demoLandD);
        
        TerritoryUtils.addTerritory(gameState, demoLandA);
        TerritoryUtils.addTerritory(gameState, demoLandB);
        TerritoryUtils.addTerritory(gameState, demoLandC);
        TerritoryUtils.addTerritory(gameState, demoLandD);

        //add neighbouring territories to each territory
        TerritoryUtils.addBorder(gameState, demoLandA, demoLandB);
        TerritoryUtils.addBorder(gameState, demoLandA, demoLandD);
        TerritoryUtils.addBorder(gameState, demoLandB, demoLandC);
        TerritoryUtils.addBorder(gameState, demoLandC, demoLandD);
        TerritoryUtils.addBorder(gameState, demoLandD, demoLandB);
	}
	

	
	@Test
	public void getUndeployedArmiesTest(){
		ArrayList<Player> players = gameState.getPlayers();
		
		assertEquals(ArmyUtils.getUndeployedArmies(players.get(0)).size(), 5);
		
		// objects should be the same in both arrays
		assertArrayEquals(ArmyUtils.getUndeployedArmies(players.get(0)).toArray(),
				players.get(0).getArmies().toArray());
		
		assertEquals(ArmyUtils.getUndeployedArmies(players.get(1)).size(), 4);
		assertArrayEquals(ArmyUtils.getUndeployedArmies(players.get(1)).toArray(),
				players.get(1).getArmies().toArray());
		
		ArmyUtils.deployArmies(players.get(0), territories.get(0), 2);
		assertEquals(ArmyUtils.getUndeployedArmies(players.get(0)).size(), 3);
		assertFalse(ArmyUtils.getUndeployedArmies(players.get(0)).equals(players.get(0).getArmies()));
	}
	
	@Test
	public void somePlayerHasUndeployedArmies(){
		ArrayList<Player> players = gameState.getPlayers();
		assertTrue(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
		
		ArmyUtils.deployArmies(players.get(0), territories.get(0), 5);
		assertTrue(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
		
		ArmyUtils.deployArmies(players.get(1), territories.get(1), 4);
		assertFalse(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
	}

	
}
