
package GameUtils;

import Common.BeforeTests;
import GameBuilders.DemoGameBuilder;
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

public class RuleUtilsTest extends BeforeTests {
	
	private State gameState;
	private Territory[] territories;

	@Before
	public void stateSetUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
		gameState = DemoGameBuilder.buildTestGame(2, 15, 15, interfaces);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
	}
	
	@Test
	public void doArmyHandoutTest() {
		// TODO: deal with continents stuff
		
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		for(int i = 0; i < 12; i++){
			ArmyUtils.deployArmies(player1, territories[i], 1);
		}
		for(int i = 0; i < 3; i++){
			ArmyUtils.deployArmies(player2, territories[i], 1);
		}
	
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 3);
		assertEquals(ArmyUtils.getUndeployedArmies(player2).size(), 12);
		
		RuleUtils.doArmyHandout(gameState, player1);
		RuleUtils.doArmyHandout(gameState, player2);
		
		assertEquals(ArmyUtils.getUndeployedArmies(player1).size(), 7);
		assertEquals(ArmyUtils.getUndeployedArmies(player2).size(), 15);
		
	}
	
	@Test
	public void applyFightResultTakeOverTest(){
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		
		ArmyUtils.deployArmies(player1, territories[0], 3);
		ArmyUtils.deployArmies(player2, territories[1], 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);
	
		FightResult fightResult = new FightResult(player1, player2, territories[0], territories[1]);
		
		fightResult.addAttackLoss();
		fightResult.addDefendLoss();
		fightResult.setAttackDiceRolled(new Integer[]{6, 2});
		fightResult.setDefendDiceRolled(new Integer[]{3});
		
		RuleUtils.applyFightResult(fightResult);
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
		
		ArmyUtils.deployArmies(player1, territories[0], 3);
		ArmyUtils.deployArmies(player2, territories[1], 3);
		
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);
		
		FightResult fightResult = new FightResult(player1, player2, territories[0], territories[1]);

		fightResult.setAttackDiceRolled(new Integer[]{3, 2});
		fightResult.setDefendDiceRolled(new Integer[]{3, 3, 3});
		fightResult.addAttackLoss();
		fightResult.addAttackLoss();
		
		RuleUtils.applyFightResult(fightResult);
		
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player1, territories[0]), 1);
		assertEquals(ArmyUtils.getNumberOfArmiesOnTerritory(player2, territories[1]), 3);
	
		assertEquals(TerritoryUtils.getPlayersTerritories(player1).size(), 1);
		assertEquals(TerritoryUtils.getPlayersTerritories(player2).size(), 1);		
	}
	

}
