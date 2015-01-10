package GameEngine;

import GameState.Army;
import GameUtils.Events.FightResult;
import GameState.Player;
import GameState.State;
import GameUtils.ArmyUtils;
import GameUtils.TerritoryUtils;
import GameUtils.PlayerUtils;
import GameUtils.RuleUtils;
import GameState.Territory;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

import static GameEngine.PlayStates.*;


public abstract class GameEngine {

	/**
	 * This is essentially the game loop. It acts a bit like a finite state machine
	 * 'PlayStates' are the states and each loop looks up the current state in the switch statement.
	 * @param gameState
	 */
	public static void play(State gameState) {
		PlayStates playState = BEGINNING_STATE;
		Player curPlayer = null;

		while (true) {

			switch (playState) {

				case BEGINNING_STATE:

					// set first player
					Arbitration.setFirstPlayer(gameState);
					// record this in the state
					curPlayer = gameState.getPlayerQueue().getCurrent();
					// move to first stage
					playState = FILLING_EMPTY_COUNTRIES;

					break;

				case FILLING_EMPTY_COUNTRIES:

					// get a list of empty territories avaiable
					HashSet<Territory> emptyTerritories = TerritoryUtils.getUnownedTerritories(gameState);
					
					// get a list of cur players undeployed armies
					ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);
					
					// ask player what country they'd like to choose
					CountrySelection toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, emptyTerritories);
					
					// deploy a single army in this place
					ArmyUtils.deployArmies(toFill.getCountry(), new ArrayList<Army>(playersUndeployedArmies.subList(0, 1)));

					
					if(!TerritoryUtils.hasEmptyTerritories(gameState)) {
						playState = USING_REMAINING_ARMIES;
					}

					curPlayer = gameState.getPlayerQueue().next();

					break;

				case USING_REMAINING_ARMIES:

					HashSet<Territory> usersTerritories = TerritoryUtils.getPlayersTerritories(curPlayer);

					playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);

					toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, usersTerritories);

					ArmySelection toDeploy = curPlayer.getInterfaceMethod().getNumberOfArmies(curPlayer, playersUndeployedArmies);

					ArmyUtils.deployArmies(toFill.getCountry(), toDeploy.getArmies());

					if(!ArmyUtils.somePlayerHasUndeployedArmies(gameState)) {
						playState = PLAYER_CONVERTING_CARDS;
					}

					curPlayer = gameState.getPlayerQueue().next();

					break;

				case PLAYER_CONVERTING_CARDS:

					curPlayer.getInterfaceMethod().getCardOptions();

					RuleUtils.doArmyHandout(gameState, curPlayer);

					playState = PLAYER_PLACING_ARMIES;

					break;

				case PLAYER_PLACING_ARMIES:

					playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);

					HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(curPlayer);

					if(playersUndeployedArmies.size() == 0) {
						playState = PLAYER_INVADING_COUNTRY;
						break;
					}

					toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, playersTerritories);

					toDeploy = curPlayer.getInterfaceMethod().getNumberOfArmies(curPlayer, playersUndeployedArmies);

					ArmyUtils.deployArmies(toFill.getCountry(), toDeploy.getArmies());

					break;

				case PLAYER_INVADING_COUNTRY:

					playersTerritories = TerritoryUtils.getPlayersTerritories(curPlayer);
					CountrySelection attacking = curPlayer.getInterfaceMethod().getTerritory(curPlayer, playersTerritories);
					if (attacking.isEndGo()) {
						playState = PLAYER_MOVING_ARMIES;
						break;
					}

					int attackingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(curPlayer, attacking.getCountry());
					int maxAttackingDice = attackingArmies > 3 ? 3 : attackingArmies - 1;

					HashSet<Territory> attackable = TerritoryUtils.getEnemyNeighbours(gameState, attacking.getCountry(), curPlayer);
					CountrySelection defending = curPlayer.getInterfaceMethod().getTerritory(curPlayer, attackable);
					if (defending.isEndGo()) {
						playState = PLAYER_MOVING_ARMIES;
						break;
					}
					Player defendingPlayer = PlayerUtils.getTerritoryOwner(gameState, defending.getCountry());
					int defendingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(defendingPlayer, defending.getCountry());
					int maxDefendingDice = defendingArmies > 2 ? 2 : defendingArmies;

					DiceSelection attackDice = curPlayer.getInterfaceMethod().getNumberOfDice(curPlayer, maxAttackingDice);
					if (attackDice.isEndGo()) {
						playState = PLAYER_MOVING_ARMIES;
						break;
					}
					DiceSelection defendDice = defendingPlayer.getInterfaceMethod().getNumberOfDice(defendingPlayer, maxDefendingDice);

					FightResult result = new FightResult(curPlayer, defendingPlayer, attacking.getCountry(), defending.getCountry());
					Arbitration.carryOutFight(result, attackDice.getNumberOfDice(), defendDice.getNumberOfDice());
					RuleUtils.applyFightResult(result);

					// if there are still surplus armies, give the option to move them
					ArrayList<Army> remainingAttackArmies = ArmyUtils.getArmiesOnTerritory(curPlayer, attacking.getCountry());
					ArrayList<Army> moveableArmies = new ArrayList<Army>(remainingAttackArmies.subList(1, remainingAttackArmies.size()));
					if (remainingAttackArmies.size() > 1) {
						ArmySelection toMove = curPlayer.getInterfaceMethod().getNumberOfArmies(curPlayer, moveableArmies);
						if (attacking.isEndGo()) {
							playState = PLAYER_MOVING_ARMIES;
							break;
						}
						ArmyUtils.moveArmies(result.getDefendingTerritory(), toMove.getArmies());
					}


					break;

				case PLAYER_MOVING_ARMIES:

					HashSet<Territory> canBeDeployedFrom = TerritoryUtils.getTerritoriesWithMoreThanOneArmy(curPlayer);
					CountrySelection source = curPlayer.getInterfaceMethod().getTerritory(curPlayer, canBeDeployedFrom);
					if (source.isEndGo()) {
						playState = PLAYER_ENDED_GO;
						break;
					}

					HashSet<Territory> canBeDeployedTo = TerritoryUtils.getFriendlyNeighbours(gameState, source.getCountry(), curPlayer);
					CountrySelection target = curPlayer.getInterfaceMethod().getTerritory(curPlayer, canBeDeployedTo);
					if (target.isEndGo()) {
						playState = PLAYER_ENDED_GO;
						break;
					}

					break;

				case PLAYER_ENDED_GO:
					gameState.getPlayerQueue().next();

					playState = PLAYER_CONVERTING_CARDS;

					break;

				default:
					break;
			}
		}
	}
}
