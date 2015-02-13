package GameEngine;

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

import static org.mockito.Mockito.*;

public class GameEngineTest{

	@Mock
	PlayerInterface player1Interface;
	
	@Mock
	PlayerInterface player2Interface;
	
	GameEngine gameEngine;
	
	
	private State gameState;
	private HashSet<Territory> territories;

	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{player1Interface, player2Interface};
		gameState = DemoGameBuilder.buildGame(2, 15, interfaces);
		territories = TerritoryUtils.getAllTerritories(gameState);
	    gameEngine = new GameEngine(gameState);
		createMockOne();
		createMockTwo();
	}
	
	
	
	
	public void createMockOne(){
		player1Interface = mock(PlayerInterface.class);

		for(int i = 1; i < 4 ; i++){
			when(player1Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject())).thenReturn(i);
		}
		
		HashSet<Territory> possibles;
		Iterator it = territories.iterator();
		possibles = territories;
		
		// mock that never resigns from attacking
		while(!possibles.isEmpty()){
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					anyBoolean(), (RequestReason) anyObject())).thenReturn(possibles.iterator().next());
			possibles.remove(it.next());
		}	
		
		// mock that always moves the maximum number of armies
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			when(player1Interface.getNumberOfArmies((Player) anyObject(), eq(i),
					(RequestReason) anyObject())).thenReturn(i);
		}
		
	
	}
	
	
	public void createMockTwo(){
		player1Interface = mock(PlayerInterface.class);

		for(int i = 1; i < 4 ; i++){
			when(player1Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject())).thenReturn(i);
		}
		
		HashSet<Territory> possibles;
		Iterator it = territories.iterator();
		possibles = territories;
		
		// mock always resigns from attacking
		while(!possibles.isEmpty()){
			when(player2Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), (RequestReason) anyObject())).thenReturn(possibles.iterator().next());
			when(player2Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(true), (RequestReason) anyObject())).thenReturn(null);	
			possibles.remove(it.next());
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
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
		PlayState returnValue = gameEngine.testCall(PlayState.FILLING_EMPTY_COUNTRIES);
		
	}
	
	@Test
	public void useARemainingArmy(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
		PlayState returnValue = gameEngine.testCall(PlayState.USING_REMAINING_ARMIES);
		
	}
	
	@Test
	public void placeArmy(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_PLACING_ARMIES);
		
	}
	
	@Test
	public void invadeCountry(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_INVADING_COUNTRY);
		
	}
	
	@Test
	public void moveArmy(){
		gameEngine.setCurrentPlayer(gameState.getPlayers().get(0));
		
		PlayState returnValue = gameEngine.testCall(PlayState.PLAYER_MOVING_ARMIES);
		
	}
	
	
	//convertCards()

}
