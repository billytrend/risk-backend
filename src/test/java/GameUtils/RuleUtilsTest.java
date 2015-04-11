
package GameUtils;


import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.Results.FightResult;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RuleUtilsTest{
	
	private State gameState;
	private Territory[] territories;
	
	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildTestGame(2, 15, interfaces);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
		GameEngine engine = new GameEngine(gameState);
	}
	
	@Test
	public void doArmyHandoutTest() {
		// TODO: deal with continents stuff
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 15);
		ArmyUtils.givePlayerNArmies(player2, 15);
		for(int i = 0; i < 12; i++){
			ArmyUtils.deployArmies(player1, territories[i], 1);
		}
		for(int i = 12; i < 15; i++){
			ArmyUtils.deployArmies(player2, territories[i], 1);
		}
	
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 43);
		assertEquals(ArmyUtils.getUndeployedArmies(player2).size(), 37);
		
		RuleUtils.doArmyHandout(gameState, player1);
		RuleUtils.doArmyHandout(gameState, player2);
		
		// player 1 should get 4 armies
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 32);
		// player2 shoulld get 3 armies
		assertEquals(ArmyUtils.getUndeployedArmies(player2).size(), 40);
		
	}
	
	@Test
	public void applyFightResultTakeOverTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		ArmyUtils.deployArmies(player1, territories[0], 3);
		ArmyUtils.deployArmies(player2, territories[1], 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);
	
		FightResult fightResult = new FightResult(player1.getId(), player2.getId(), 
				territories[0].getId(), territories[1].getId());
		
		fightResult.addAttackLoss();
		fightResult.addDefendLoss();
		fightResult.setAttackDiceRolled(new Integer[]{6, 2});
		fightResult.setDefendDiceRolled(new Integer[]{3});
		
		fightResult.applyChange(gameState);
		
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territories[0]), 1);
		assertTrue(TerritoryUtils.getPlayersTerritories(player1).contains(territories[1]));
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territories[1]), 1);
		assertFalse(TerritoryUtils.getPlayersTerritories(player2).contains(territories[1]));
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 2);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 0);		
	}
	
	@Test
	public void applyFightResultTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		ArmyUtils.givePlayerNArmies(player1, 5);
		ArmyUtils.givePlayerNArmies(player2, 5);
		ArmyUtils.deployArmies(player1, territories[0], 3);
		ArmyUtils.deployArmies(player2, territories[1], 3);
		
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);
		
		FightResult fightResult = new FightResult(player1.getId(), player2.getId(), territories[0].getId(), territories[1].getId());

		fightResult.setAttackDiceRolled(new Integer[]{3, 2});
		fightResult.setDefendDiceRolled(new Integer[]{3, 3, 3});
		fightResult.addAttackLoss();
		fightResult.addAttackLoss();
		
		fightResult.applyChange(gameState);
		
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territories[0]), 1);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, territories[1]), 3);
	
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);		
	}
	

}
