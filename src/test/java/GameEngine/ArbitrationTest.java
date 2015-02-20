package GameEngine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.TerritoryUtils;
import GameUtils.Results.FightResult;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import java.util.ArrayList;
import java.util.Collections;

public class ArbitrationTest {

	private State gameState;
	private Territory[] territories;

	@Before
	public void stateSetUp() {
		PlayerInterface[] interfaces = new PlayerInterface[] {
				new DumbBotInterface(), new DumbBotInterface() };
		gameState = DemoGameBuilder.buildTestGame(2, 5, 2, interfaces);
		territories = new Territory[TerritoryUtils.getAllTerritories(gameState)
				.size()];
		TerritoryUtils.getAllTerritories(gameState).toArray(territories);
	}

	@Test
	public void dieThrowTest() {
		Integer result = null;
		ArrayList<Integer> results = new ArrayList<Integer>();
		int tryTimes = 30;
		for (int i = 0; i < 30; i++) {
			result = Arbitration.dieThrowWrapper();
			results.add(result);
			assertTrue((0 < result) && (result < 7));
		}

		// make sure that the results are different
		assertFalse(Collections.frequency(results, result) == tryTimes);
	}

	@Test
	public void carryOutFightTest() {
		ArrayList<Player> players = gameState.getPlayers();
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		FightResult fightResult = new FightResult(player1, player2,
				territories[0], territories[1]);

		fightResult = Arbitration.carryOutFight(fightResult, 3, 2);
		Integer[] attackDice = fightResult.getAttackDiceRolled();
		Integer[] defendDice = fightResult.getDefendDiceRolled();
		assertEquals(attackDice.length, 3);
		assertEquals(defendDice.length, 2);

		for (int i = 0; i < 3; i++) {
			assertTrue((0 < attackDice[i]) && (attackDice[i] < 7));
		}
		for (int i = 0; i < 2; i++) {
			assertTrue((0 < defendDice[i]) && (defendDice[i] < 7));
		}

		int attackLoss = fightResult.getAttackersLoss();
		int defendLoss = fightResult.getDefendersLoss();
		assertTrue((attackLoss < 3) && (attackLoss > -1));
		assertTrue((defendLoss < 3) && (defendLoss > -1));

	}

}