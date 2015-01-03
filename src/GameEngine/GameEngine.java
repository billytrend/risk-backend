package GameEngine;

import GameState.*;
import GameState.Events.FightResult;
import GameState.GameBuilders.DemoGame;
import GameState.StateUtils.ArmyUtils;
import GameState.StateUtils.MapUtils;
import GameState.StateUtils.OwnershipUtils;
import GameState.StateUtils.RuleUtils;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

import static GameEngine.PlayStates.*;

public abstract class GameEngine {

	// armies assigned to the users at the beginning of the game
	static int armiesAtTheStart = 5;

	public static void main(String[] args) {
		play();
	}

	public static void play() {
		PlayStates playState = BEGINNING_STATE;
		State gameState = DemoGame.buildGame();
		Player curPlayer = null;

		while (true) {

			switch (playState) {

				case BEGINNING_STATE:


					Arbitration.setFirstPlayer(gameState);

					curPlayer = gameState.getPlayerQueue().getCurrent();

					playState = FILLING_EMPTY_COUNTRIES;

					break;

				case FILLING_EMPTY_COUNTRIES:

					HashSet<Territory> emptyTerritories = MapUtils.getUnownedTerritories(gameState);

					ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);

					CountrySelection toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, emptyTerritories);

					OwnershipUtils.deployArmies(toFill.getCountry(), new ArrayList<Army>(playersUndeployedArmies.subList(0, 1)));

					if(!MapUtils.hasEmptyTerritories(gameState)) {
						playState = USING_REMAINING_ARMIES;
					}

					curPlayer = gameState.getPlayerQueue().next();

					break;

				case USING_REMAINING_ARMIES:

					HashSet<Territory> usersTerritories = MapUtils.getPlayersTerritories(curPlayer);

					playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);

					toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, usersTerritories);

					ArmySelection toDeploy = curPlayer.getInterfaceMethod().getNumberOfArmies(curPlayer, playersUndeployedArmies);

					OwnershipUtils.deployArmies(toFill.getCountry(), toDeploy.getArmies());

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

					HashSet<Territory> playersTerritories = MapUtils.getPlayersTerritories(curPlayer);

					if(playersUndeployedArmies.size() == 0) {
						playState = PLAYER_INVADING_COUNTRY;
						break;
					}

					toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, playersTerritories);

					toDeploy = curPlayer.getInterfaceMethod().getNumberOfArmies(curPlayer, playersUndeployedArmies);

					OwnershipUtils.deployArmies(toFill.getCountry(), toDeploy.getArmies());

					break;

				case PLAYER_INVADING_COUNTRY:

					playersTerritories = MapUtils.getPlayersTerritories(curPlayer);
					CountrySelection attacking = curPlayer.getInterfaceMethod().getTerritory(curPlayer, playersTerritories);
					if (attacking.isEndGo()) {
						playState = PLAYER_MOVING_ARMIES;
						break;
					}

					int attackingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(curPlayer, attacking.getCountry());
					int maxAttackingDice = attackingArmies > 3 ? 3 : attackingArmies - 1;

					HashSet<Territory> attackable = MapUtils.getEnemyNeighbours(gameState, attacking.getCountry(), curPlayer);
					CountrySelection defending = curPlayer.getInterfaceMethod().getTerritory(curPlayer, attackable);
					if (defending.isEndGo()) {
						playState = PLAYER_MOVING_ARMIES;
						break;
					}
					Player defendingPlayer = OwnershipUtils.getTerritoryOwner(gameState, defending.getCountry());
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

					HashSet<Territory> canBeDeployedFrom = MapUtils.getTerritoriesWithMoreThanOneArmy(curPlayer);
					CountrySelection source = curPlayer.getInterfaceMethod().getTerritory(curPlayer, canBeDeployedFrom);
					if (source.isEndGo()) {
						playState = PLAYER_ENDED_GO;
						break;
					}

					HashSet<Territory> canBeDeployedTo = MapUtils.getFriendlyNeighbours(gameState, source.getCountry(), curPlayer);
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
