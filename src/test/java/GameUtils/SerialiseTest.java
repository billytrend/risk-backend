package GameUtils;
import GameBuilders.DemoGameBuilder;
import GameState.State;
import GameState.Territory;
import GeneralUtils.Serialisers.GameStateSerialiser;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import org.junit.Before;
import org.junit.Test;


public class SerialiseTest {

	private State gameState;
	private Territory[] territories;
	
	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildGame(interfaces);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
		
	//	ArmyUtils.deployArmies(gameState.getPlayerQueue().getCurrent(), TerritoryUtils.getAllTerritories(gameState).contains(), 2);
	}
	@Test
	public void test() {
		GameStateSerialiser s = new GameStateSerialiser();
	}

}
