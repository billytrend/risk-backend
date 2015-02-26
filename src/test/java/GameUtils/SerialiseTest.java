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
		gameState = DemoGameBuilder.buildGame(4, interfaces);
		territories = new Territory[gameState.getTerritories().vertexSet().size()];
		gameState.getTerritories().vertexSet().toArray(territories);
		
		

		//ArmyUtils.deployArmies(gameState.getPlayerQueue().getCurrent(), TerritoryUtils.getUnownedTerritories(state), 2);
	}
	@Test
	public void test() {
		GameStateSerialiser s = new GameStateSerialiser();
		System.out.println(s.serialize(gameState, null, null));
	}

}
