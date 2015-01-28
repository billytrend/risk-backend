package GameEngine;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.Results.FightResult;
import GameUtils.PlayerUtils;
import GameUtils.RuleUtils;
import GameUtils.TerritoryUtils;
import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

import static GameEngine.PlayState.*;

/**
 * An instance of this class represents a game that
 * is currently being played. Several games can be played
 * at a time.
 * 
 */
public class GameEngine implements Runnable {

	private State gameState;
	private Player currentPlayer;
	private PlayState playState = BEGINNING_STATE;
	
	public GameEngine(State state) {
		this.gameState = state;
	}
	
	public State getState(){
		return gameState;
	}
	
	@Override
	public void run() {
		try {
			play();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This is the game loop.
	 * 'PlayStates' are the states and each loop looks up the 
	 * current state in the iterateGame function.
	 */
	private void play() throws InterruptedException {
		while (true) {
			if(!iterateGame()) return;
		}
	}

	/**
	 * This function takes the game forward a step, changing
	 * its current state. If it's looped, the game will play.
	 *
	 * @throws InterruptedException
	 * @throws NullPointerException
	 */
	private boolean iterateGame() throws InterruptedException, NullPointerException {

		switch (this.playState) {
			case BEGINNING_STATE:
			//	System.out.println("\nBEGIN");
				this.playState = begin();
				gameState.print();
				break;

			case FILLING_EMPTY_COUNTRIES:
			//	System.out.println("\nFILLING EMPTY COUNTRIES");
				this.playState = fillAnEmptyCountry();
				break;

			case USING_REMAINING_ARMIES:
			//	System.out.println("\nUSING REMAINING ARMIES");
				this.playState = useARemainingArmy();
				break;

			case PLAYER_CONVERTING_CARDS:
			//	System.out.println("\nCARDS");
				this.playState = convertCards();
				// TODO: why is this here?
				ArmyUtils.givePlayerNArmies(currentPlayer, 1);
				break;

			case PLAYER_PLACING_ARMIES:
			//	System.out.println("\nPLAYER PLACING ARMIES");
				this.playState = placeArmy();
				break;

			case PLAYER_INVADING_COUNTRY:
			//	System.out.println("\nINVADING");
				this.playState = invadeCountry(); 
				gameState.print();
				break;

			case PLAYER_MOVING_ARMIES:
			//	System.out.println("\nMOVING ARMIES");
				this.playState = moveArmy();
				gameState.print();
				break;

			case PLAYER_ENDED_GO:
			//	System.out.println("\nEND GO");
				this.playState = endGo();
				break;
				
			case END_GAME:
				System.out.println("\nEND GAME!");
				return false;
			
			default:
				break;
		}
		
		return true;
	}

	
	/**
	 * Function called at the start of a game.
	 * It sets the first player.
	 * 
	 * @return
	 */
	private PlayState begin() {
		// set first player
		Arbitration.setFirstPlayer(this.gameState);
		// record this in the state
		this.currentPlayer = gameState.getPlayerQueue().getCurrent();
		// move to first stage
		return FILLING_EMPTY_COUNTRIES;
	}

	
	/**
	 * This method is called at the beginning of the game.
	 * It allows the current player to place an army on one of
	 * the unoccupied territories. It also transfers the game 
	 * to the next state once it recognises that there are no
	 * empty territories.
	 *  
	 * @return
	 */
	private PlayState fillAnEmptyCountry() {

		// get a list of empty territories available
		HashSet<Territory> emptyTerritories = TerritoryUtils.getUnownedTerritories(gameState);

		// player specifies the country
		CountrySelection toFill = (CountrySelection) currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, emptyTerritories, false).await();

		// deploy a single army in this place
		ArmyUtils.deployArmies(currentPlayer, toFill.getCountry(), 1);

		endGo();

		if (!TerritoryUtils.hasEmptyTerritories(gameState)) {
			System.out.println("ALL COUNTRIES TAKEN");
			return USING_REMAINING_ARMIES;
		}

		return FILLING_EMPTY_COUNTRIES;

	}

	
	/**
	 * Method called at the beginning of the game, once all the
	 * territories have been taken by the players. It allows the 
	 * current player to place an army unit on one of their territories.
	 * If the player has no armies left then the turn is passed to
	 * the next player. 
	 * The method can also detect that none of the players has remaining
	 * armies and cause the game to transfer to the next state.
	 * 
	 * @return
	 */
	private PlayState useARemainingArmy() {

		// get a list of a players undeployed armies
		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);
		
		// if a  player doesnt have any undeployed armies
		if (playersUndeployedArmies.size() == 0) {
			
			// the current player ends their turn
			endGo();

			// if none of the other players has undeployed armies
			// the game goes to the next state
			if (!ArmyUtils.somePlayerHasUndeployedArmies(gameState)) {
				return PLAYER_CONVERTING_CARDS;
			}
			
			// keep going for the sake of the player who still has remaining armies
			return USING_REMAINING_ARMIES;
		}

		// get a list of the players territories
		HashSet<Territory> usersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);
		
		// ask a player what country they want to pick
		CountrySelection toFill = (CountrySelection) currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, usersTerritories, false).await();

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
		
		currentPlayer.getCommunicationMethod().getCardOptions();

		RuleUtils.doArmyHandout(gameState, currentPlayer);

		return PLAYER_PLACING_ARMIES;

	}

	/**
	 * Method used within the entire game. It is called any time
	 * the player receives new armies. It allows the current player
	 * to choose the amount of armies they want to place and the
	 * destination (from the payers territories only). After that
	 * decision is made the specified amount of armies is placed. 
	 * 
	 * @return
	 */
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
		CountrySelection toFill = (CountrySelection) currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, playersTerritories, false).await();

		// find out how many armies the player want to deploy there 
		ArmySelection toDeploy = (ArmySelection) currentPlayer.getCommunicationMethod()
				.getNumberOfArmies(currentPlayer, playersUndeployedArmies.size()).await();

		// do the deployment!
		ArmyUtils.deployArmies(currentPlayer, toFill.getCountry(), toDeploy.getArmies());

		return PLAYER_PLACING_ARMIES;
	}

	
	/**
	 * A function that carries out the whole attack - checks its validity,
	 * carries out a fight and applies the result of the battle.
	 * 
	 * @return
	 */
	private PlayState invadeCountry() {
		
		// get the territories of the current player
		HashSet<Territory> possibleAttackingTerritories = TerritoryUtils
				.getPossibleAttackingTerritories(gameState, currentPlayer);
		// find out which country the player wants to attack from
		CountrySelection attacking = (CountrySelection) currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, possibleAttackingTerritories, true).await();
		
		if(attacking == null){
			// System.out.println("PLAYER DOESNT WANT TO INVADE");
			return PLAYER_MOVING_ARMIES;
		}
		
		// get the enemy neighbours of the country
		HashSet<Territory> attackable = TerritoryUtils
				.getEnemyNeighbours(gameState, attacking.getCountry(), currentPlayer);
		
			
		// ask the player which country he wants to attack
		CountrySelection defending = (CountrySelection) currentPlayer
				.getCommunicationMethod().getTerritory(currentPlayer, attackable, false).await();
		

		// find out who owns this fated land
		Player defendingPlayer = PlayerUtils.getTerritoryOwner(gameState, defending.getCountry());

		
		// work out the max number of armies that may attack
		// and how many may defend as per rules
		int attackingArmies = ArmyUtils
				.getNumberOfArmiesOnTerritory(currentPlayer, attacking.getCountry());
		System.out.println("attacking armies: " + attackingArmies);
		int maxAttackingDice = (attackingArmies > 3) ? 3 : attackingArmies - 1;
		
		int defendingArmies = ArmyUtils
				.getNumberOfArmiesOnTerritory(defendingPlayer, defending.getCountry());
		int maxDefendingDice = defendingArmies > 2 ? 2 : defendingArmies;

		// ask the players how many they would like to use
		DiceSelection attackDice = (DiceSelection) currentPlayer.
				getCommunicationMethod().getNumberOfDice(currentPlayer, maxAttackingDice).await();
		DiceSelection defendDice = (DiceSelection) defendingPlayer.
				getCommunicationMethod().getNumberOfDice(defendingPlayer, maxDefendingDice).await();

		// create an object to represent the fight
		FightResult result = new FightResult(currentPlayer, defendingPlayer, 
				attacking.getCountry(), defending.getCountry());
	
		// decide the results of the fight
		Arbitration.carryOutFight(result, attackDice.getNumberOfDice(), defendDice.getNumberOfDice());
		
		// apply the results of the fight
		RuleUtils.applyFightResult(result);

		// if the attacking player won and they still have surplus armies,
		// give the option to move them
		if(result.getDefendersLoss() == defendingArmies){
			//	&& (attackingArmies - result.getAttackersLoss() - attackDice.getNumberOfDice()) > 1) {
	
			if((attackingArmies - result.getAttackersLoss() - attackDice.getNumberOfDice()) > 1)
				moveMoreArmies(result);
			
			if(PlayerUtils.playerIsOut(result.getDefender())){
				gameState.getPlayerQueue().removePlayer(result.getDefender());
				if(checkTheEndOfGame())
					return END_GAME;
			}
	
		}

		return PLAYER_INVADING_COUNTRY;

	}
	
	
	
	/**
	 * The method id called after a player took over a new territory.
	 * Allows the player to move the specified amount of armies from 
	 * the attacking territory to new acquired one. They can also decide
	 * not to move any armies.
	 *
	 * TODO: Not sure if they can...
	 *  
	 * @param result
	 */
	private void moveMoreArmies(FightResult result){
		ArrayList<Army> remainingAttackArmies = ArmyUtils
				.getArmiesOnTerritory(currentPlayer, result.getAttackingTerritory());
		
		// let the player decide how many armies they want to move
		ArmySelection toMove = (ArmySelection) currentPlayer.getCommunicationMethod()
				.getNumberOfArmies(currentPlayer, remainingAttackArmies.size() - 1).await();
		
		ArmyUtils.moveArmies(result.getAttacker(), result.getAttackingTerritory(), 
				result.getDefendingTerritory(), toMove.getArmies());
	}
	
	
	/**
	 * Called every time a player takes over a territory. The end of
	 * the game is recognised by checking the number of players.
	 * 
	 * @return
	 */
	private boolean checkTheEndOfGame(){
		if(gameState.getPlayerQueue().getNumberOfCurrentPlayers() == 1){
			return true;
	}
		return false;
	}
	

	/**
	 * The method moves a specified amount of armies from a territory
	 * chosen by a user to another 'friendly' one of their choice.
	 * They might decide not to move any armies at all.
	 * It is called every time the player is about to end their turn. 
	 * 
	 * @return
	 */
	private PlayState moveArmy() {
		
		// get a list of territories a player can deploy from
		HashSet<Territory> canBeDeployedFrom = TerritoryUtils
				.getDeployable(gameState, currentPlayer);
		
		// find out which one the player wants to move from
		CountrySelection source = (CountrySelection) currentPlayer
				.getCommunicationMethod().getTerritory(currentPlayer, canBeDeployedFrom, true).await();
		
		//------------------------------------
		// HANDLE HERE NOT MOVING RESPONCE? - null country selection?
		if(source == null){
			System.out.println("PLAYER DOESNT WANT TO MOVE");
			return PLAYER_ENDED_GO;
		}

		// get a list of territories a player can deploy too
		HashSet<Territory> canBeDeployedTo = TerritoryUtils
				.getFriendlyNeighbours(gameState, source.getCountry(), currentPlayer);
		// get the choice made
		CountrySelection target = (CountrySelection) currentPlayer
				.getCommunicationMethod().getTerritory(currentPlayer, canBeDeployedTo, false).await();

		int numberOfArmiesThatMayBeMoved = ArmyUtils
				.getNumberOfMoveableArmies(currentPlayer, source.getCountry());
		
		ArmySelection toMove = (ArmySelection) currentPlayer.getCommunicationMethod()
				.getNumberOfArmies(currentPlayer, numberOfArmiesThatMayBeMoved).await();
		
		ArmyUtils.moveArmies(currentPlayer, source.getCountry(), target.getCountry(), toMove.getArmies());
		
		
		return PLAYER_MOVING_ARMIES;
	}

	
	/**
	 * Called every time a player ends their turn.
	 * 
	 * @return
	 */
	private PlayState endGo() {
		currentPlayer = gameState.getPlayerQueue().next();
		return PLAYER_CONVERTING_CARDS;
	}

}