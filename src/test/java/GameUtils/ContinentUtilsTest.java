package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameState.Continent;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContinentUtilsTest {
	
	private State gameState;
	private Territory[] territories;
	
	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildGame(interfaces);
		territories = new Territory[gameState.getTerritories().vertexSet().size()];
		gameState.getTerritories().vertexSet().toArray(territories);
		GameEngine engine = new GameEngine(gameState);
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
	
	@Test
	public void playerOwnsContinentTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player p = players.get(0);
		Continent contAB = gameState.getContinents().get(0);
		Continent contCD = gameState.getContinents().get(1);
		
		ArmyUtils.deployArmies(p, territories[0], 1);
		ArmyUtils.deployArmies(p, territories[1], 1);
		assertTrue(ContinentUtils.checkPlayerOwnsContinent(p, contAB));
		assertTrue(ContinentUtils.getPlayersContinents(gameState, p).contains(contAB));
		
		ArmyUtils.deployArmies(p, territories[2], 1);
		ArmyUtils.deployArmies(p, territories[3], 1);
		assertTrue(ContinentUtils.checkPlayerOwnsContinent(p, contCD));
		assertTrue(ContinentUtils.getPlayersContinents(gameState, p).contains(contAB));
		assertTrue(ContinentUtils.getPlayersContinents(gameState, p).contains(contCD));
	}
	

}
