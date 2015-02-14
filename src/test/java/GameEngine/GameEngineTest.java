package GameEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import GameBuilders.DemoGameBuilder;
import GameState.Player;
import GameState.State;
import GameState.Territory;
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
			when(player1Interface.getTerritory((Player) anyObject(), argThat(new isGivenHashSet(subset)),
					anyBoolean(), (RequestReason) anyObject())).thenReturn(subset.iterator().next());
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

		for(HashSet<Territory> subset : subsets){
			when(player1Interface.getTerritory((Player) anyObject(), argThat(new isGivenHashSet(subset)),
					eq(false), (RequestReason) anyObject())).thenReturn(subset.iterator().next());
			when(player1Interface.getTerritory((Player) anyObject(), argThat(new isGivenHashSet(subset)),
					eq(true), (RequestReason) anyObject())).thenReturn(null);
		}	
		
		// mock that always moves minimum number of armies
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			if(i == 0){
				when(player1Interface.getNumberOfArmies((Player) anyObject(), eq(i),
						eq(RequestReason.REINFORCEMENT_PHASE))).thenReturn(0);
			}
			else{
				when(player1Interface.getNumberOfArmies((Player) anyObject(), eq(i),
						eq(RequestReason.PLACING_ARMIES_PHASE))).thenReturn(1);
			}
		}
		
	}
	
	
	@Test
	public void fillAnEmptyCountryTest(){
		
		Player player1 = gameState.getPlayers().get(0);
		gameEngine.setCurrentPlayer(player1);
		
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 4);
		
		PlayState returnValue = gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		
		assertEquals(TerritoryUtils.getUnownedTerritories(gameState).size(), 3);
		assertFalse(TerritoryUtils.getUnownedTerritories(gameState).
				contains(TerritoryUtils.getPlayersTerritories(player1)));
		
	}
	
	@Test
	public void useARemainingArmy(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
	//	PlayState returnValue = gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		
	}
	
	@Test
	public void placeArmy(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
	//	PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		
	}
	
	@Test
	public void invadeCountry(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
	//	PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_INVADING_COUNTRY);
		
	}
	
	@Test
	public void moveArmy(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
	//	PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_MOVING_ARMIES);
		
	}
	
	
	//convertCards()

}
