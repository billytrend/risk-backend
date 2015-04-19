package GameUtils;

import GameBuilders.RiskMapGameBuilder;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContinentUtilsTest {
	
	private State gameState;
	private ArrayList<Continent> continents;
	
	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		gameState = RiskMapGameBuilder.buildGame(interfaces);
		continents = gameState.getContinents();
	}
	
	@Test
	public void getContinentsTest() {
		assertEquals(continents.size(), 6);
		Continent northAmerica = continents.get(0);
		assertEquals(northAmerica.getId(), "north_america");
		assertEquals(northAmerica.getTerritories().size(), 9);
		assertEquals(northAmerica.getArmyReward(), 5);
		Continent australia = continents.get(5);
		assertEquals(australia.getId(), "australia");
		assertEquals(australia.getTerritories().size(), 4);
		assertEquals(australia.getArmyReward(), 2);
	
	}
	
	@Test
	public void playerOwnsContinentTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player p = players.get(0);
		Continent northAmerica = gameState.getContinents().get(0);
		Continent southAmerica = gameState.getContinents().get(1);
		ArrayList<Territory> territoriesNA = northAmerica.getTerritories();
		assertEquals(9, territoriesNA.size());
		ArmyUtils.givePlayerNArmies(p, 20);
		assertTrue(ArmyUtils.getUndeployedArmies(p).size() > 0);
		for(Territory territory:territoriesNA){
			ArmyUtils.deployArmies(p, territory, 1);
		}
		assertTrue(ContinentUtils.checkPlayerOwnsContinent(p, northAmerica));
		assertTrue(ContinentUtils.getPlayersContinents(gameState, p).contains(northAmerica));
		assertFalse(ContinentUtils.checkPlayerOwnsContinent(p, southAmerica));
		assertFalse(ContinentUtils.getPlayersContinents(gameState, p).contains(southAmerica));
		ArrayList<Territory> territoriesSA = southAmerica.getTerritories();
		for(Territory territory:territoriesSA){
			ArmyUtils.deployArmies(p, territory, 1);
		}
		assertTrue(ContinentUtils.checkPlayerOwnsContinent(p, southAmerica));
		assertTrue(ContinentUtils.checkPlayerOwnsContinent(p, northAmerica));
		assertTrue(ContinentUtils.getPlayersContinents(gameState, p).contains(southAmerica));
		assertTrue(ContinentUtils.getPlayersContinents(gameState, p).contains(northAmerica));
	}
	@Test
	public void getContinentByIdTest(){
		
		assertEquals(null, ContinentUtils.getContinentById(gameState,"notacontinent"));
		assertEquals(continents.get(0), ContinentUtils.getContinentById(gameState,"north_america"));
		assertEquals(continents.get(1), ContinentUtils.getContinentById(gameState,"south_america"));
		assertEquals(continents.get(2), ContinentUtils.getContinentById(gameState,"europe"));
		assertEquals(continents.get(3), ContinentUtils.getContinentById(gameState,"africa"));
		assertEquals(continents.get(4), ContinentUtils.getContinentById(gameState,"asia"));
		assertEquals(continents.get(5), ContinentUtils.getContinentById(gameState,"australia"));
	}
	
	@Test
	public void getContinentPayoutTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player p = players.get(0);
		ArmyUtils.givePlayerNArmies(p, 30);
		assertEquals(0, ContinentUtils.getContinentPayout(gameState, p));
		for(Territory t : continents.get(0).getTerritories()){
			ArmyUtils.deployArmies(p, t, 1);
		}
		assertEquals(5, ContinentUtils.getContinentPayout(gameState, p));
		
		
		for(Territory t : continents.get(1).getTerritories()){
			ArmyUtils.deployArmies(p, t, 1);
		}
		assertEquals(7, ContinentUtils.getContinentPayout(gameState, p));
		
		for(Territory t : continents.get(3).getTerritories()){
			ArmyUtils.deployArmies(p, t, 1);
		}
		assertEquals(10, ContinentUtils.getContinentPayout(gameState, p));
		
		for(Territory t : continents.get(5).getTerritories()){
			ArmyUtils.deployArmies(p, t, 1);
		}
		assertEquals(12, ContinentUtils.getContinentPayout(gameState, p));
		
	}

}
