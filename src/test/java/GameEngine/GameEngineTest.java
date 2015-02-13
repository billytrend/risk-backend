package GameEngine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import Common.BeforeTests;
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

public class GameEngineTest extends BeforeTests {

	@Mock
	PlayerInterface player1Interface;
	
	@Mock
	PlayerInterface player2Interface;
	
	
	private State gameState;
	private HashSet<Territory> territories;

	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{player1Interface, player2Interface};
		gameState = DemoGameBuilder.buildGame(2, 15, interfaces);
		territories = TerritoryUtils.getAllTerritories(gameState);
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
		while(!possibles.isEmpty()){
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.ATTACK_CHOICE_FROM))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(true), eq(RequestReason.ATTACK_CHOICE_FROM))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.ATTACK_CHOICE_TO))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.PLACING_ARMIES_PHASE))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.PLACING_ARMIES_SET_UP))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.PLACING_REMAINING_ARMIES_PHASE))).thenReturn(possibles.iterator().next());
			
			possibles.remove(it.next());
		}		
		
		
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			when(player1Interface.getNumberOfArmies((Player) anyObject(), i,
					eq(RequestReason.REINFORCEMENT_PHASE))).thenReturn(i);
			
			when(player1Interface.getNumberOfArmies((Player) anyObject(), i,
					eq(RequestReason.PLACING_ARMIES_PHASE))).thenReturn(i);
			
			when(player1Interface.getNumberOfArmies((Player) anyObject(), i,
					eq(RequestReason.POST_ATTACK_MOVEMENT))).thenReturn(i);
		}
		
	
	}
	
	public void createMockTwo(){
		player2Interface = mock(PlayerInterface.class);

		for(int i = 1; i < 4 ; i++){
			when(player1Interface.getNumberOfDice((Player) anyObject(), eq(i),
				(RequestReason) anyObject())).thenReturn(i);
		}
		
		
		HashSet<Territory> possibles;
		Iterator it = territories.iterator();
		possibles = territories;
		while(!possibles.isEmpty()){
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.ATTACK_CHOICE_FROM))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(true), eq(RequestReason.ATTACK_CHOICE_FROM))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.ATTACK_CHOICE_TO))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.PLACING_ARMIES_PHASE))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.PLACING_ARMIES_SET_UP))).thenReturn(possibles.iterator().next());
			
			when(player1Interface.getTerritory((Player) anyObject(), eq(possibles),
					eq(false), eq(RequestReason.PLACING_REMAINING_ARMIES_PHASE))).thenReturn(possibles.iterator().next());
			
			possibles.remove(it.next());
		}		
		
		
		int predictedMaxNumOfArmies = 200;
		for(int i = 0; i < predictedMaxNumOfArmies; i++){
			when(player1Interface.getNumberOfArmies((Player) anyObject(), i,
					eq(RequestReason.REINFORCEMENT_PHASE))).thenReturn(i);
			
			when(player1Interface.getNumberOfArmies((Player) anyObject(), i,
					eq(RequestReason.PLACING_ARMIES_PHASE))).thenReturn(i);
			
			when(player1Interface.getNumberOfArmies((Player) anyObject(), i,
					eq(RequestReason.POST_ATTACK_MOVEMENT))).thenReturn(i);
		}
		
	}
	
	

}
