package GameEngine;

import GameBuilders.DemoGameBuilder;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.PlayerInterface;
import com.esotericsoftware.minlog.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameEngineTest{

	PlayerInterface player1Interface = mock(PlayerInterface.class);
	PlayerInterface player2Interface = mock(PlayerInterface.class);
	
	Comparator comparator = new TerritoryIdComparator();
	ArrayList<Territory> sortedTerritories;
	private State gameState;
	TestableGameEngine gameEngine;

	@Before
	public void stateSetUp(){
		Log.NONE();
		PlayerInterface[] interfaces = new PlayerInterface[]{player1Interface, player2Interface};
		
		// creates a game with 4 territories
		gameState = DemoGameBuilder.buildGame(interfaces);
		HashSet<Territory> territories = TerritoryUtils.getAllTerritories(gameState);
	    gameEngine = new TestableGameEngine(gameState);
        ArmyUtils.giveStartingArmies(gameState);

		sortedTerritories = new ArrayList<Territory>();
		sortedTerritories.addAll(territories);
		Collections.sort(sortedTerritories, comparator);
		createMockOne();
		createMockTwo();
	}

	
	@Test
	public void fillAnEmptyCountryTest(){
		
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 4);
		
		PlayState returnValue = gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		
		assertEquals(returnValue, PlayState.FILLING_EMPTY_COUNTRIES);
		assertEquals(gameEngine.getCurrentPlayer(), player2);
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 3);
		
		
		// the mock always chooses first out of alphabetically sorted list 
		// of territories
		Territory chosenTerritory = sortedTerritories.get(0);
		assertTrue(TerritoryUtils.getPlayersTerritories(player1).contains(chosenTerritory));
		assertFalse(TerritoryUtils.getUnownedTerritories(gameState).contains(chosenTerritory));
		
		// territory was just taken so it should host only 1 army
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, chosenTerritory).size(), 1);
		
		while(TerritoryUtils.hasEmptyTerritories(gameState)){
			returnValue = gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		}
		
		assertEquals(returnValue, PlayState.USING_REMAINING_ARMIES);
	}
	
	
	
	@Test
	public void useARemainingArmyTest(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		ArmyUtils.deployArmies(player1, sortedTerritories.get(0), 1);
		ArmyUtils.deployArmies(player1, sortedTerritories.get(1), 1);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(2), 1);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(3), 1);
		
		HashSet<Territory> player1Territories = TerritoryUtils.getPlayersTerritories(player1);
		HashSet<Territory> player2Territories = TerritoryUtils.getPlayersTerritories(player2);

		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 38);
		
		PlayState returnValue = gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		
		assertEquals(returnValue, PlayState.USING_REMAINING_ARMIES);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 37);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(0)).size(), 2);
		
		assertEquals(gameEngine.getCurrentPlayer(), player2);
		
		while(ArmyUtils.getUndeployedArmies(player2).size() > 0){
			gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		}
		returnValue = gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		
		assertFalse(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
		assertEquals(returnValue, PlayState.PLAYER_BEGINNING_TURN);
	}
	
	
	@Test
	public void placeArmyTest(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		
		ArmyUtils.deployArmies(player1, sortedTerritories.get(0), 1);
		ArmyUtils.deployArmies(player1, sortedTerritories.get(1), 1);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(2), 1);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(3), 1);
		
		// this player goes for the maximum 
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		assertEquals(returnValue, PlayState.PLAYER_PLACING_ARMIES);
		
		// the player does not end his go after placing armies
		assertEquals(gameEngine.getCurrentPlayer(), player1);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 0);
		
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(0)).size(), 39);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(1)).size(), 1);
		
		returnValue = gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		assertEquals(returnValue, PlayState.PLAYER_INVADING_COUNTRY);
		
		///////////////
		
		gameEngine.nextPlayer();
		gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);


		assertEquals(ArmyUtils.getUndeployedArmies(player2).size(), 37);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player2, sortedTerritories.get(2)).size(), 2);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player2, sortedTerritories.get(3)).size(), 1);
		
		while(ArmyUtils.getUndeployedArmies(player2).size() > 0){
			gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		}
		returnValue = gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		
		assertEquals(returnValue, PlayState.PLAYER_INVADING_COUNTRY);
	}
	
	
	@Test
	public void invadeCountryNoTakeOverTest(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		ArmyUtils.deployArmies(player1, sortedTerritories.get(0), 10);
		ArmyUtils.deployArmies(player1, sortedTerritories.get(1), 5);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(2), 5);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(3), 10);
		
		//player one attacks territory 2 from territory 0
		// player1 throws 3 dice and player2 throws 2 dice
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_INVADING_COUNTRY);
		
		assertEquals(returnValue, PlayState.PLAYER_INVADING_COUNTRY);
		// number of armies on other territories dont change
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, sortedTerritories.get(1)), 5);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, sortedTerritories.get(3)), 10);		
		
		// the total loss of armies should be 2 since 2 dice are compared 
		
		int attackersLoss = 10 - ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(0)).size();
		int defendersLoss = 5 - ArmyUtils.getArmiesOnTerritory(player2, sortedTerritories.get(2)).size();
		assertEquals(attackersLoss + defendersLoss, 2);
		
		// no way the territory was taken over
		assertTrue(TerritoryUtils.getPlayersTerritories(player2).contains(sortedTerritories.get(2)));
		assertFalse(TerritoryUtils.getPlayersTerritories(player1).contains(sortedTerritories.get(2)));
	}
	
	@Test
	public void invadeCountryResignTest(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		gameEngine.nextPlayer();
		
		ArmyUtils.deployArmies(player1, sortedTerritories.get(0), 10);
		ArmyUtils.deployArmies(player1, sortedTerritories.get(1), 5);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(2), 5);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(3), 10);

		assertEquals(gameEngine.getCurrentPlayer(), player2);
		// player 2 resigns from attacking
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_INVADING_COUNTRY);

		assertEquals(returnValue, PlayState.PLAYER_MOVING_ARMIES);
		
		// number of armies on territories dont change
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, sortedTerritories.get(1)), 5);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, sortedTerritories.get(3)), 10);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, sortedTerritories.get(0)), 10);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, sortedTerritories.get(2)), 5);
	}
	
	@Test
	public void invadeCountryTakeOverTest(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		ArmyUtils.deployArmies(player1, sortedTerritories.get(0), 14);
		ArmyUtils.deployArmies(player1, sortedTerritories.get(1), 1);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(2), 1);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(3), 10);
		
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, sortedTerritories.get(0)), 14);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, sortedTerritories.get(2)), 1);
		
		//player one attacks territory 2 from territory 0
		// player1 throws 3 dice and player2 throws 1 dice
		int tries = -1;
		while(TerritoryUtils.getPlayersTerritories(player2).contains(sortedTerritories.get(2))){
			gameEngine.testCall(PlayState.PLAYER_INVADING_COUNTRY);
			tries++;
		}
		
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, sortedTerritories.get(1)), 1);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, sortedTerritories.get(3)), 10);		
		
		// the territory was taken over
		assertTrue(TerritoryUtils.getPlayersTerritories(player1).contains(sortedTerritories.get(2)));
		assertFalse(TerritoryUtils.getPlayersTerritories(player2).contains(sortedTerritories.get(2)));
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 3);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);
		
		// and the maximum number of armies were moved to the new territory (player1 mock defined)
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(2)).size(), 13 - tries);
	}
	
	
	@Test
	public void moveArmyTest(){		
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		ArmyUtils.deployArmies(player1, sortedTerritories.get(0), 5);
		ArmyUtils.deployArmies(player1, sortedTerritories.get(1), 10);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(2), 5);
		ArmyUtils.deployArmies(player2, sortedTerritories.get(3), 10);
		
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_MOVING_ARMIES);
		
		// player moves all possible armies
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(0)).size(), 1);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, sortedTerritories.get(1)).size(), 14);
		
		gameEngine.nextPlayer();
		
		returnValue = gameEngine.testCall(PlayState.PLAYER_MOVING_ARMIES);
		
		// player resigns from moving armies
		assertEquals(ArmyUtils.getArmiesOnTerritory(player2, sortedTerritories.get(2)).size(), 5);
		assertEquals(ArmyUtils.getArmiesOnTerritory(player2, sortedTerritories.get(3)).size(), 10);
		
		assertEquals(returnValue, PlayState.PLAYER_BEGINNING_TURN);
		
	}
	
	
	//convertCards()

	
	private void createMockOne(){
		for(int i = 1; i < 4 ; i++){
			when(player1Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject(), (Territory) anyObject(), (Territory) anyObject())).thenReturn(i);
		}
		
		ArrayList<HashSet<Territory>> subsets = getSubsets(sortedTerritories);
		List<Territory> sorted;
		
		for(HashSet<Territory> subset : subsets){
			sorted = new ArrayList<Territory>();
			sorted.addAll(subset);
			
			// always return the first territory from alphabetically sorted list
			// == the output is deterministic
			Collections.sort(sorted, comparator);
			if(sorted.size() > 0){
				when(player1Interface.getTerritory((Player) anyObject(), eq(subset),null,
						anyBoolean(), (RequestReason) anyObject())).thenReturn(sorted.get(0));
			}

		}	

		// mock that always moves the maximum number of armies
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			when(player1Interface.getNumberOfArmies((Player) anyObject(), eq(i),
					(RequestReason) anyObject(), (Territory) anyObject(), (Territory) anyObject())).thenReturn(i);
		}

	}
	
	
	private void createMockTwo(){
		for(int i = 1; i < 4 ; i++){
			when(player2Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject(), (Territory) anyObject(), (Territory) anyObject())).thenReturn(i);
		}
		
		ArrayList<HashSet<Territory>> subsets = getSubsets(sortedTerritories);
		List<Territory> sorted;

		// resigning mock
		for(HashSet<Territory> subset : subsets){
			sorted = new ArrayList<Territory>();
			sorted.addAll(subset);
			
			Collections.sort(sorted, comparator);
			if(sorted.size() > 0){
				when(player2Interface.getTerritory((Player) anyObject(), eq(subset), null,
						eq(false), (RequestReason) anyObject())).thenReturn(sorted.get(0));

				// resign if you can
				when(player2Interface.getTerritory((Player) anyObject(), eq(subset), null,
						eq(true), (RequestReason) anyObject())).thenReturn(null);
			}
		}	
		
		// mock that always moves minimum number of armies
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			if(i == 0){
				when(player2Interface.getNumberOfArmies((Player) anyObject(), eq(i),
						eq(RequestReason.REINFORCEMENT_PHASE), (Territory) anyObject(), (Territory) anyObject())).thenReturn(0);
			}else{
				when(player2Interface.getNumberOfArmies((Player) anyObject(), eq(i),
						eq(RequestReason.PLACING_ARMIES_PHASE), (Territory) anyObject(), (Territory) anyObject())).thenReturn(1);
			}
		}
	}
	

	// method that returns all subsets of given array of territories
	// used for accurate mocking
	private ArrayList<HashSet<Territory>> getSubsets(ArrayList<Territory> set) {

		ArrayList<HashSet<Territory>> allSubsets = new ArrayList<HashSet<Territory>>();

		if(set.size() == 0){
			allSubsets.add(new HashSet<Territory>());
		}
		if (set.size() != 0) {
			ArrayList<Territory> reducedSet = new ArrayList<Territory>();
			reducedSet.addAll(set);
			
			Territory ter = reducedSet.remove(0);
			ArrayList<HashSet<Territory>> subsets = getSubsets(reducedSet);
			
			if(subsets.size() > 0)
				allSubsets.addAll(subsets);

			subsets = getSubsets(reducedSet);
			for (HashSet<Territory> subset : subsets) {
				subset.add(ter);
			}

			allSubsets.addAll(subsets);
		}
		return allSubsets;
	}
	
	
	// compares territories in terms of the alphabetical order of their ids
	public class TerritoryIdComparator implements Comparator {
		@Override
		public int compare(Object ob1, Object ob2) {
			Territory ter1 = (Territory) ob1;
			Territory ter2 = (Territory) ob2;
			return (ter1.getId().compareToIgnoreCase(ter2.getId()));
		}
	}
}
