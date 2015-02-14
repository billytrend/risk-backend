package GameEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import GameBuilders.DemoGameBuilder;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameEngineTest{

	PlayerInterface player1Interface = mock(PlayerInterface.class);
	
	PlayerInterface player2Interface = mock(PlayerInterface.class);
	
	TestableGameEngine gameEngine;
	
	
	private State gameState;
	private HashSet<Territory> territories;

	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{player1Interface, player2Interface};
		gameState = DemoGameBuilder.buildGame(2, 15, interfaces);
		territories = TerritoryUtils.getAllTerritories(gameState);
	    gameEngine = new TestableGameEngine(gameState);
		createMockOne();
		createMockTwo();
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
	
	
	private void createMockOne(){

		for(int i = 1; i < 4 ; i++){
			when(player1Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject())).thenReturn(i);
		}
		

		ArrayList<Territory> allTerrs = new ArrayList<Territory>();
		allTerrs.addAll(territories);
		
		ArrayList<HashSet<Territory>> subsets = getSubsets(allTerrs);

		for(HashSet<Territory> subset : subsets){
			if(subset.size() > 0){
				when(player1Interface.getTerritory((Player) anyObject(), eq(subset),
						anyBoolean(), (RequestReason) anyObject())).thenReturn(subset.iterator().next());
			}
			else{
				when(player1Interface.getTerritory((Player) anyObject(), eq(subset),
						anyBoolean(), (RequestReason) anyObject())).thenReturn(null);
			}
		}	

		// mock that always moves the maximum number of armies
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			when(player1Interface.getNumberOfArmies((Player) anyObject(), eq(i),
					(RequestReason) anyObject())).thenReturn(i);
		}

	}
	
	
	private void createMockTwo(){

		for(int i = 1; i < 4 ; i++){
			when(player2Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject())).thenReturn(i);
		}
		

		ArrayList<Territory> allTerrs = new ArrayList<Territory>();
		allTerrs.addAll(territories);
		
		ArrayList<HashSet<Territory>> subsets = getSubsets(allTerrs);

		// resigning mock
		for(HashSet<Territory> subset : subsets){
			if(subset.size() > 0){
				when(player2Interface.getTerritory((Player) anyObject(), eq(subset),
						eq(false), (RequestReason) anyObject())).thenReturn(subset.iterator().next());
			}
			else{
				when(player2Interface.getTerritory((Player) anyObject(), eq(subset),
						eq(false), (RequestReason) anyObject())).thenReturn(null);
			}
			when(player2Interface.getTerritory((Player) anyObject(),  eq(subset),
					eq(true), (RequestReason) anyObject())).thenReturn(null);
		}	
		
		// mock that always moves minimum number of armies
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			if(i == 0){
				when(player2Interface.getNumberOfArmies((Player) anyObject(), eq(i),
						eq(RequestReason.REINFORCEMENT_PHASE))).thenReturn(0);
			}
			else{
				when(player2Interface.getNumberOfArmies((Player) anyObject(), eq(i),
						eq(RequestReason.PLACING_ARMIES_PHASE))).thenReturn(1);
			}
		}
		
	}
	
	
	@Test
	public void fillAnEmptyCountryTest(){
		
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 4);
		
		PlayState returnValue = gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 3);
		assertFalse(TerritoryUtils.getUnownedTerritories(gameState).
				contains(TerritoryUtils.getPlayersTerritories(player1)));
		assertEquals(ArmyUtils.getArmiesOnTerritory(player1, 
				TerritoryUtils.getPlayersTerritories(player1).iterator().next()).size(), 1);
		assertEquals(returnValue, PlayState.FILLING_EMPTY_COUNTRIES);
		assertEquals(gameEngine.getCurrentPlayer(), player2);
		
		gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		returnValue = gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 0);
		assertFalse(TerritoryUtils.hasEmptyTerritories(gameState));
		
		assertEquals(returnValue, PlayState.USING_REMAINING_ARMIES);
	}
	
	
	@Test
	public void useARemainingArmy(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		Iterator<Territory> it = territories.iterator();
		while(it.hasNext()){
			ArmyUtils.deployArmies(player1, it.next(), 1);
			if(it.hasNext())
				ArmyUtils.deployArmies(player2, it.next(), 1);
		}
		
		HashSet<Territory> player1Territories = TerritoryUtils.getPlayersTerritories(player1);
		HashSet<Territory> player2Territories = TerritoryUtils.getPlayersTerritories(player2);
		assertEquals(player1Territories.size(), 2);
		assertEquals(player2Territories.size(), 2);
		
		it = player1Territories.iterator();
		while(it.hasNext()){
			assertEquals(ArmyUtils.getArmiesOnTerritory(player1, it.next()).size(), 1);
		}

		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 13);
		PlayState returnValue = gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		
		assertEquals(returnValue, PlayState.USING_REMAINING_ARMIES);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 12);
		
		it = player1Territories.iterator();
		
		int twoArmyCountriesCount = 0;
		while(it.hasNext()){
			if(ArmyUtils.getArmiesOnTerritory(player1, it.next()).size() == 2)
				if(twoArmyCountriesCount > 0)
					fail();
				else
					twoArmyCountriesCount++;
		}
		
		while(ArmyUtils.getUndeployedArmies(player2).size() > 0){
			gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		}
		returnValue = gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		
		assertFalse(ArmyUtils.somePlayerHasUndeployedArmies(gameState));
		assertEquals(returnValue, PlayState.PLAYER_CONVERTING_CARDS);
	}
	
	
	@Test
	public void placeArmy(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		Iterator<Territory> it = territories.iterator();
		while(it.hasNext()){
			ArmyUtils.deployArmies(player1, it.next(), 1);
			if(it.hasNext())
				ArmyUtils.deployArmies(player2, it.next(), 1);
		}
		
		// this player goes for the maximum 
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		
		assertEquals(returnValue, PlayState.PLAYER_PLACING_ARMIES);
		assertEquals(gameEngine.getCurrentPlayer(), player1);
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 0);
		
		it = TerritoryUtils.getPlayersTerritories(player1).iterator();
		int armiesOnFirstTerritory = ArmyUtils.getArmiesOnTerritory(player1, it.next()).size();
		if(armiesOnFirstTerritory == 1){
			assertEquals(ArmyUtils.getArmiesOnTerritory(player1, it.next()), 14);
		}
		else{
			assertEquals(armiesOnFirstTerritory, 14);
			assertEquals(ArmyUtils.getArmiesOnTerritory(player1, it.next()).size(), 1);
		}
		
	}
	
	
	@Test
	public void invadeCountry(){
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_INVADING_COUNTRY);
		
	}
	
	
	@Test
	public void moveArmy(){		
		Player player1 = gameState.getPlayers().get(0);
		Player player2 = gameState.getPlayers().get(1);
		gameEngine.setFirstPlayer(0);
		
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_MOVING_ARMIES);
		
	}
	
	
	//convertCards()

}
