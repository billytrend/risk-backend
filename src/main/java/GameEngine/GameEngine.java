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
		ArmyUtils.deployArmies(currentPlayer, toFill.getCountry(), 1);

		endGo();

		if (!TerritoryUtils.hasEmptyTerritories(gameState)) {
			return USING_REMAINING_ARMIES;
		}

		return FILLING_EMPTY_COUNTRIES;

	}

	private PlayState useARemainingArmy() {

		// get a list of a players undeployed armies
		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);
		
		// check player has undeployed armies
		if (playersUndeployedArmies.size() == 0) {

			endGo();

			// check if any players have undeployed armies
			if (!ArmyUtils.somePlayerHasUndeployedArmies(gameState)) {
				return PLAYER_CONVERTING_CARDS;
			}
			
			// keep going for the sake of the player who still has remaining armies
			return USING_REMAINING_ARMIES;
		}

		// get a list of the players territories
		HashSet<Territory> usersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);
		
		// ask a player what country they want to pic
		CountrySelection toFill = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, usersTerritories).await();

		// deploy the armies
		ArmyUtils.deployArmies(currentPlayer, toFill.getCountry(), 1);
		
		endGo();
		
		return USING_REMAINING_ARMIES;

	}

	/**
	 * TODO: all things card related!!
	 * @return
	 */
	private PlayState convertCards() {
		
		currentPlayer.getInterfaceMethod().getCardOptions();

		RuleUtils.doArmyHandout(gameState, currentPlayer);

		return PLAYER_PLACING_ARMIES;

	}

	private PlayState placeArmy() {

		// get a list of players undeployed armies
		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);

		// check if player has any armys left to place
		if (playersUndeployedArmies.size() == 0) {
			return PLAYER_INVADING_COUNTRY;
		}

		// get players territories
		HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);
		
		// find out which country the player wants to place in
		CountrySelection toFill = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, playersTerritories).await();

		// find out how many armies the player want to deploy there 
		ArmySelection toDeploy = (ArmySelection) currentPlayer.getInterfaceMethod().getNumberOfArmies(currentPlayer, playersUndeployedArmies.size()).await();

		// do the deployment!
		ArmyUtils.deployArmies(currentPlayer, toFill.getCountry(), toDeploy.getArmies());

		return PLAYER_PLACING_ARMIES;
	}

	private PlayState invadeCountry() {

		// get the territories of the current player
		HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);
		// find out which country the player wants to attack from
		CountrySelection attacking = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, playersTerritories).await();

		// get the enemy neighbours of the country
		HashSet<Territory> attackable = TerritoryUtils.getEnemyNeighbours(gameState, attacking.getCountry(), currentPlayer);
		// ask the player which country he wants to attack
		CountrySelection defending = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, attackable).await();
		// find out who owns this fated land
		Player defendingPlayer = PlayerUtils.getTerritoryOwner(gameState, defending.getCountry());

		// work out the max number of armies that may attack and how many may defend as per rules
		int attackingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(currentPlayer, attacking.getCountry());
		int maxAttackingDice = attackingArmies > 3 ? 3 : attackingArmies - 1;
		int defendingArmies = ArmyUtils.getNumberOfArmiesOnTerritory(defendingPlayer, defending.getCountry());
		int maxDefendingDice = defendingArmies > 2 ? 2 : defendingArmies;

		// ask the players how many they would like to use
		DiceSelection attackDice = (DiceSelection) currentPlayer.getInterfaceMethod().getNumberOfDice(currentPlayer, maxAttackingDice).await();
		DiceSelection defendDice = (DiceSelection) defendingPlayer.getInterfaceMethod().getNumberOfDice(defendingPlayer, maxDefendingDice).await();

		// create an object to represent the fight
		FightResult result = new FightResult(currentPlayer, defendingPlayer, attacking.getCountry(), defending.getCountry());
		// decide the results of the fight
		Arbitration.carryOutFight(result, attackDice.getNumberOfDice(), defendDice.getNumberOfDice());
		// apply the results of the fight
		RuleUtils.applyFightResult(result);

		// if the attacking player won and they still have surplus armies, give the option to move them
		if(result.getDefendersLoss() == defendingArmies && (attackingArmies - result.getAttackersLoss() - attackDice.getNumberOfDice()) > 1) {
			ArrayList<Army> remainingAttackArmies = ArmyUtils.getArmiesOnTerritory(currentPlayer, attacking.getCountry());
			ArrayList<Army> moveableArmies = new ArrayList<Army>(remainingAttackArmies.subList(1, remainingAttackArmies.size()));
			ArmySelection toMove = (ArmySelection) currentPlayer.getInterfaceMethod().getNumberOfArmies(currentPlayer, moveableArmies.size()).await();
			ArmyUtils.moveArmies(result.getAttacker(), result.getDefendingTerritory(), result.getAttackingTerritory(), toMove.getArmies());
		}

		return PLAYER_INVADING_COUNTRY;

	}

	private PlayState moveArmy() {
		// get a list of territories a player can deploy from
		HashSet<Territory> canBeDeployedFrom = TerritoryUtils.getTerritoriesWithMoreThanOneArmy(currentPlayer);
		// find out which one the player wants to move from
		CountrySelection source = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, canBeDeployedFrom);

		// get a list of territories a player can deploy too
		HashSet<Territory> canBeDeployedTo = TerritoryUtils.getFriendlyNeighbours(gameState, source.getCountry(), currentPlayer);
		// get the choice made
		CountrySelection target = (CountrySelection) currentPlayer.getInterfaceMethod().getTerritory(currentPlayer, canBeDeployedTo);

		int numberOfArmiesThatMayBeMoved = ArmyUtils.getNumberOfMoveableArmies(currentPlayer, source.getCountry());
		
		
		return PLAYER_MOVING_ARMIES;
	}

	private PlayState endGo() {
		currentPlayer = gameState.getPlayerQueue().next();

		return PLAYER_CONVERTING_CARDS;

	}

}