package GameEngine;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngineTest.TerritoryIdComparator;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import com.esotericsoftware.minlog.Log;

public class GameSubsetTest {
	
		
	@Test
	public void smallMapTest(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		State gameState = DemoGameBuilder.buildTestGame(2, 15, 4, interfaces);
		ArrayList<Player> players = gameState.getPlayers();
		WinConditions conditions = new WinConditions(1, 3);
		GameEngine gameEngine = new GameEngine(gameState, conditions);
		gameEngine.run();
		
		assertEquals(PlayerUtils.countPlayers(gameState), 2);
		if(TerritoryUtils.getPlayersTerritories(players.get(0)).size() == 3)
			assertEquals(TerritoryUtils.getPlayersTerritories(players.get(1)).size(), 1);
		else if((TerritoryUtils.getPlayersTerritories(players.get(1)).size() == 3))
			assertEquals(TerritoryUtils.getPlayersTerritories(players.get(0)).size(), 1);
		else
			fail();
	}
	
	@Test
	public void noConditionTest(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		State gameState = DemoGameBuilder.buildTestGame(2, 30, 10, interfaces);
		ArrayList<Player> players = gameState.getPlayers();
		GameEngine gameEngine = new GameEngine(gameState);
		gameEngine.run();
		
		assertEquals(PlayerUtils.countPlayers(gameState), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(PlayerUtils.getPlayersInGame(gameState).get(0)).size(),
				TerritoryUtils.getAllTerritories(gameState).size());
	}
	
	@Test
	public void mediumMapTest(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		State gameState = DemoGameBuilder.buildTestGame(2, 30, 16, interfaces);
		ArrayList<Player> players = gameState.getPlayers();
		WinConditions conditions = new WinConditions(1, 14);
		GameEngine gameEngine = new GameEngine(gameState, conditions);
		gameEngine.run();
		
		assertEquals(PlayerUtils.countPlayers(gameState), 2);
		if(TerritoryUtils.getPlayersTerritories(players.get(0)).size() == 14)
			assertEquals(TerritoryUtils.getPlayersTerritories(players.get(1)).size(), 2);
		else if((TerritoryUtils.getPlayersTerritories(players.get(1)).size() == 2))
			assertEquals(TerritoryUtils.getPlayersTerritories(players.get(0)).size(), 14);
		else
			fail();
	}
	
	@Test
	public void morePlayersTest(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
				new DumbBotInterface(), new DumbBotInterface(), new DumbBotInterface()};
		State gameState = DemoGameBuilder.buildTestGame(5, 100, 70, interfaces);
		ArrayList<Player> players = gameState.getPlayers();
		WinConditions conditions = new WinConditions(1, 23);
		GameEngine gameEngine = new GameEngine(gameState, conditions);
		gameEngine.run();
		
		assertEquals(PlayerUtils.countPlayers(gameState), 5);
		
		Player winner = null;
		for(Player player : players){
			if(TerritoryUtils.getPlayersTerritories(player).size() == 23){
				winner = player;
			}
		}

		assertFalse(winner == null);
		
		for(Player player : players){
			if(!winner.equals(player))
				assertTrue(TerritoryUtils.getPlayersTerritories(player).size() < 23);
		}
		
	}
		
}
