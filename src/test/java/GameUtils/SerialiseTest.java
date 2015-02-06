package GameUtils;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import GameState.Territory;
import GameUtils.TerritoryUtils;
import GeneralUtils.Serialisers.GameStateSerialiser;
import GeneralUtils.Serialisers.LobbySerialiser;


public class SerialiseTest {

	private State gameState;
	private Territory[] territories;
	
	@Before
	public void stateSetUp(){
		gameState = DemoGameBuilder.buildGame(4, 10);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
		
	//	ArmyUtils.deployArmies(gameState.getPlayerQueue().getCurrent(), TerritoryUtils.getAllTerritories(gameState).contains(), 2);
	}
	@Test
	public void test() {
		GameStateSerialiser s = new GameStateSerialiser();
		System.out.println(s.serialize(gameState, null, null));
	}

}
