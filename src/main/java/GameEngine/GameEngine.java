package GameEngine;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.Events.FightResult;
import GameUtils.PlayerUtils;
import GameUtils.RuleUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

import static GameEngine.PlayState.*;


public class GameEngine implements Runnable {

	private State gameState;
	private Player currentPlayer;
	private PlayState playState = BEGINNING_STATE;

	public GameEngine(State state) {
		this.gameState = state;
	}
	
	/**
	 * This is the game loop.
	 * 'PlayStates' are the states and each loop looks up the current state in the iterateGame function.
	 */
	private void play() throws InterruptedException {
		while (true) {
			iterateGame();
		}
	}

	/**
	 * This function takes the game forward a step, if it's looped, the game will play.
	 *
	 * @throws InterruptedException
	 * @throws NullPointerException
	 */
	private void iterateGame() throws InterruptedException, NullPointerException {

		switch (this.playState) {

			case BEGINNING_STATE:
				this.playState = begin();
				break;

			case FILLING_EMPTY_COUNTRIES:
				this.playState = fillAnEmptyCountry();
				break;

			case USING_REMAINING_ARMIES:
				this.playState = useARemainingArmy();
				break;

			case PLAYER_CONVERTING_CARDS:
				this.playState = convertCards();
				break;

			case PLAYER_PLACING_ARMIES:
				this.playState = placeArmy();
				break;

			case PLAYER_INVADING_COUNTRY:
				this.playState = invadeCountry();
				break;

			case PLAYER_MOVING_ARMIES:
				this.playState = moveArmy();
				break;

			case PLAYER_ENDED_GO:
				this.playState = endGo();
				break;

			default:
				break;
		}

	}


	@Override
	public void run() {
		try {
			play();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private PlayState begin() {
		// set first player
		Arbitration.setFirstPlayer(this.gameState);
		// record this in the state
		this.currentPlayer = gameState.getPlayerQueue().getCurrent();
		// move to first stage
		return FILLING_EMPTY_COUNTRIES;
	}

	private PlayState fillAnEmptyCountry() {

		// get a list of empty territories avaiable
		HashSet<Territory> emptyTerritories = TerritoryUtils.getUnownedTerritories(gameState);

		// get a list of cur players undeployed armies
		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);

		// ask player what country they'd like to choose
		CountrySelection toFill = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, emptyTerritories).await();

		// deploy a single army in this place
		ArmyUtils.deployArmies(toFill.getCountry(), new ArrayList<Army>(playersUndeployedArmies.subList(0, 1)));


		if (!TerritoryUtils.hasEmptyTerritories(gameState)) {
			return USING_REMAINING_ARMIES;
		}

		endGo();

		return FILLING_EMPTY_COUNTRIES;

	}

	private PlayState useARemainingArmy() {
		HashSet<Territory> usersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);

		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);

		CountrySelection toFill = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, usersTerritories).await();

		ArmySelection toDeploy = (ArmySelection) currentPlayer.getInterfaceMethod().getNumberOfArmies(currentPlayer, playersUndeployedArmies).await();

		ArmyUtils.deployArmies(toFill.getCountry(), toDeploy.getArmies());

		if (!ArmyUtils.somePlayerHasUndeployedArmies(gameState)) {
			return PLAYER_CONVERTING_CARDS;
		}

		endGo();

		return USING_REMAINING_ARMIES;

	}

	private PlayState convertCards() {
		currentPlayer.getInterfaceMethod().getCardOptions();

		RuleUtils.doArmyHandout(gameState, currentPlayer);

		return PLAYER_PLACING_ARMIES;

	}

	private PlayState placeArmy() {

		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);

		HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);

		if (playersUndeployedArmies.size() == 0) {
			return PLAYER_INVADING_COUNTRY;
		}

		CountrySelection toFill = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, playersTerritories).await();

		ArmySelection toDeploy = (ArmySelection) currentPlayer.getInterfaceMethod().getNumberOfArmies(currentPlayer, playersUndeployedArmies).await();

		ArmyUtils.deployArmies(toFill.getCountry(), toDeploy.getArmies());

		return PLAYER_PLACING_ARMIES;

	}

	private PlayState invadeCountry() {

		HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);
		CountrySelection attacking = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, playersTerritories).await();
		if (attacking.isEndGo()) {
			return PLAYER_MOVING_ARMIES;
		}

		int attackingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(currentPlayer, attacking.getCountry());
		int maxAttackingDice = attackingArmies > 3 ? 3 : attackingArmies - 1;

		HashSet<Territory> attackable = TerritoryUtils.getEnemyNeighbours(gameState, attacking.getCountry(), currentPlayer);
		CountrySelection defending = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, attackable).await();
		if (defending.isEndGo()) {
			return PLAYER_MOVING_ARMIES;
		}
		Player defendingPlayer = PlayerUtils.getTerritoryOwner(gameState, defending.getCountry());
		int defendingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(defendingPlayer, defending.getCountry());
		int maxDefendingDice = defendingArmies > 2 ? 2 : defendingArmies;

		DiceSelection attackDice = (DiceSelection) currentPlayer.getInterfaceMethod().getNumberOfDice(currentPlayer, maxAttackingDice).await();
		if (attackDice.isEndGo()) {
			return PLAYER_MOVING_ARMIES;
		}
		DiceSelection defendDice = (DiceSelection) defendingPlayer.getInterfaceMethod().getNumberOfDice(defendingPlayer, maxDefendingDice).await();

		FightResult result = new FightResult(currentPlayer, defendingPlayer, attacking.getCountry(), defending.getCountry());
		Arbitration.carryOutFight(result, attackDice.getNumberOfDice(), defendDice.getNumberOfDice());
		RuleUtils.applyFightResult(result);

		// if there are still surplus armies, give the option to move them
		ArrayList<Army> remainingAttackArmies = ArmyUtils.getArmiesOnTerritory(currentPlayer, attacking.getCountry());
		ArrayList<Army> moveableArmies = new ArrayList<Army>(remainingAttackArmies.subList(1, remainingAttackArmies.size()));
		if (remainingAttackArmies.size() > 1) {
			ArmySelection toMove = (ArmySelection) currentPlayer.getInterfaceMethod().getNumberOfArmies(currentPlayer, moveableArmies).await();
			if (attacking.isEndGo()) {
				return PLAYER_MOVING_ARMIES;
			}
			ArmyUtils.moveArmies(result.getDefendingTerritory(), toMove.getArmies());
		}

		return PLAYER_INVADING_COUNTRY;

	}

	private PlayState moveArmy() {
		HashSet<Territory> canBeDeployedFrom = TerritoryUtils.getTerritoriesWithMoreThanOneArmy(currentPlayer);
		CountrySelection source = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, canBeDeployedFrom);
		if (source.isEndGo()) {
			return PLAYER_ENDED_GO;
		}

		HashSet<Territory> canBeDeployedTo = TerritoryUtils.getFriendlyNeighbours(gameState, source.getCountry(), currentPlayer);
		CountrySelection target = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, canBeDeployedTo);
		if (target.isEndGo()) {
			return PLAYER_ENDED_GO;
		}

		return PLAYER_MOVING_ARMIES;
	}

	private PlayState endGo() {
		gameState.getPlayerQueue().next();

		return PLAYER_CONVERTING_CARDS;

	}

}