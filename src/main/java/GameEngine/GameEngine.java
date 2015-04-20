package GameEngine;

import GameState.*;
import GameUtils.*;
import GameUtils.Results.*;
import PlayerInput.DumbBotInterfaceProtocol;
import PlayerInput.PlayerInterface;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;

import static GameEngine.PlayState.*;
import static com.esotericsoftware.minlog.Log.debug;

/**
 * An instance of this class represents a game that
 * is currently being played. Several games can be played
 * at a time.
 * 
 */
public class GameEngine implements Runnable {

	protected State gameState;
	protected Player currentPlayer = null;
	private PlayState playState = BEGINNING_STATE;
    private PlayState previousPlayState;
	private boolean currentPlayerHasTakenCountry = false;
	private StateChangeRecord changeRecord;
	private WinConditions winConditions;
	private ArbitrationAbstract arbitration;
	
	HashSet<Territory> possibleAttackingTerritories;
	
	public StateChangeRecord getStateChangeRecord(){
		return changeRecord;
	}
	
// ==================== to be used by protocol ====================	
	public PlayState getPlayState(){
		return playState;
	}
	private boolean countryTakenInPreviousTurn;
	
	public boolean isCountryTaken(){
		return countryTakenInPreviousTurn;
	}
	
	public void resetCountryTaken(){
		countryTakenInPreviousTurn = false;
	}
// ================================================================
	
	
	
	public GameEngine(State state, WinConditions conditions) {
		this(state);
		winConditions = conditions;
	}
	
	public GameEngine(State state) {
		this.gameState = state;
		changeRecord = new StateChangeRecord(state.getPlayersIds(), state.getTerritoryIds(), state.getPlayers().get(0).getArmies().size());
		winConditions = new WinConditions();
        arbitration = new Arbitration();
    }
	
	public GameEngine(State state, ArbitrationAbstract arbitration) {
		this(state);
        this.arbitration = arbitration;
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
        applyAndReportChange(gameState, new GameStart("", BEGINNING_STATE, gameState));
        while (true) {
			if(!iterateGame()) return;
		}
	}
    
    public void applyAndReportChange(State state, Change change) {
        change.applyChange(state);
        for (Player player :  gameState.getPlayers()) {
            player.getCommunicationMethod().reportStateChange(change);
        }
        for (PlayerInterface ghost : gameState.getGhosts()) {
            ghost.reportStateChange(change);
        }
        changeRecord.addStateChange(change);
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
				debug("\nBEGIN");
				this.playState = begin();
				gameState.print();
				break;

			case FILLING_EMPTY_COUNTRIES:
				debug("\nFILLING EMPTY COUNTRIES");
				this.playState = fillAnEmptyCountry();
				break;

			case USING_REMAINING_ARMIES:
				debug("\nUSING REMAINING ARMIES");
				this.playState = useARemainingArmy();
				break;

			case PLAYER_BEGINNING_TURN:
				debug("\nCARDS");
				this.playState = giveAdditionalArmies();
				currentPlayer.logTurn();
				break;

			case PLAYER_PLACING_ARMIES:
				debug("\nPLAYER PLACING ARMIES");
				this.playState = placeArmy();
				break;

			case PLAYER_INVADING_COUNTRY:
				debug("\nINVADING");
				this.playState = invadeCountry(); 
				gameState.print();
				break;

			case PLAYER_MOVING_ARMIES:
                debug("\nMOVING ARMIES");
                this.playState = moveArmy();
                gameState.print();
				break;

			case PLAYER_ENDED_GO:
				debug("\nEND GO");
				this.playState = endGo();
				break;
				
			case END_GAME:
				debug("\nEND GAME!");
				gameState.setWinner(currentPlayer);
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


		// set first player if they havent been set from the protocol side
		if(currentPlayer == null){
			arbitration.setFirstPlayer(this.gameState);
			// record this in the state
			this.currentPlayer = gameState.getPlayerQueue().getCurrent();
            applyAndReportChange(gameState, new PlayerChange(currentPlayer.getId()));
        }

        // do initial army handout
        int numOfArmies = ArmyUtils.getStartingArmies(gameState);

        ArrayList<Player> players = gameState.getPlayers();

        for(Player p : players){
            ArmyHandout handout = new ArmyHandout(p.getId(), numOfArmies, playState);
            applyAndReportChange(gameState, handout);
        }


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
	protected PlayState fillAnEmptyCountry() {

		// get a list of empty territories available
		HashSet<Territory> emptyTerritories = TerritoryUtils.getUnownedTerritories(gameState);
		
		// player specifies the country
		debug(currentPlayer.getClass().toString());
		Territory toFill = currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, emptyTerritories, null, false, RequestReason.PLACING_ARMIES_SET_UP);
		debug(toFill.getId());

		// deploy a single army in this place

        Change stateChange = new ArmyPlacement(currentPlayer.getId(), toFill.getId(), 1, FILLING_EMPTY_COUNTRIES);
        applyAndReportChange(gameState, stateChange);

		endGo();
		
		if (!TerritoryUtils.hasEmptyTerritories(gameState)) {
			debug("ALL COUNTRIES TAKEN");
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
	protected PlayState useARemainingArmy() {

		// get a list of a players undeployed armies
		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);
		
		// if a  player doesnt have any undeployed armies
		if (playersUndeployedArmies.size() == 0) {
			
			// the current player ends their turn
			//endGo();

			// if none of the other players has undeployed armies
			// the game goes to the next state
			if (!ArmyUtils.somePlayerHasUndeployedArmies(gameState)) {
				return PLAYER_BEGINNING_TURN;
			}
			
			// keep going for the sake of the player who still has remaining armies
			endGo();
			return USING_REMAINING_ARMIES;
		}

		// get a list of the players territories
		HashSet<Territory> usersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);
		
		// ask a player what country they want to pick
		Territory toFill = currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, usersTerritories, null, false, RequestReason.PLACING_REMAINING_ARMIES_PHASE);

		// deploy the armies
        Change stateChange = new ArmyPlacement(currentPlayer.getId(), toFill.getId(), 1, USING_REMAINING_ARMIES);
        applyAndReportChange(gameState, stateChange);

		endGo();
	
		return USING_REMAINING_ARMIES;

	}
	
	private PlayState giveAdditionalArmies(){

		int payout = TerritoryUtils.getPlayersTerritories(currentPlayer).size()/3;

		if(payout < 3) payout = 3;

		payout += ContinentUtils.getContinentPayout(gameState, currentPlayer);

		payout += convertCards();

        ArmyHandout handout = new ArmyHandout(currentPlayer.getId(), payout, playState);

        applyAndReportChange(gameState, handout);

		return PLAYER_PLACING_ARMIES;
	}

	/**
	 * TODO: all things card related!!
	 * @return
	 */
	private int convertCards() {

        ArrayList<Triplet<Card, Card, Card>> possibleCombinations = CardUtils.getPossibleCardCombinations(gameState, currentPlayer);

		if (possibleCombinations.size() == 0) return 0;

		Triplet<Card, Card, Card> choice = currentPlayer.getCommunicationMethod().getCardChoice(currentPlayer, possibleCombinations);

        HashSet<Territory> territoriesOwned = CardUtils.getTerritoriesOnCardsThatPlayersOwn(currentPlayer, choice);

        if (territoriesOwned.size() > 0) {

            ArmyHandout handout = new ArmyHandout(currentPlayer.getId(), 2, playState);
            applyAndReportChange(gameState, handout);

            Territory territoryChoice = currentPlayer.getCommunicationMethod().getTerritory(currentPlayer, territoriesOwned, null, false, RequestReason.PLACING_ARMIES_PHASE);
            // deploy the armies
            Change stateChange = new ArmyPlacement(currentPlayer.getId(), territoryChoice.getId(), 1, null);
            applyAndReportChange(gameState, stateChange);

            territoryChoice = currentPlayer.getCommunicationMethod().getTerritory(currentPlayer, territoriesOwned, null, false, RequestReason.PLACING_ARMIES_PHASE);
            // deploy the armies
            stateChange = new ArmyPlacement(currentPlayer.getId(), territoryChoice.getId(), 1, null);
            applyAndReportChange(gameState, stateChange);

        }

        if (choice == null) return 0;
        int cardPayout = CardUtils.getCurrentArmyPayout(currentPlayer, choice);

        CardUtils.releaseCards(choice);

		return cardPayout;

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
	protected PlayState placeArmy() {
		
		// get a list of players undeployed armies
		ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(currentPlayer);

		// check if player has any armys left to place
		if (playersUndeployedArmies.size() == 0) {
			return PLAYER_INVADING_COUNTRY;
		}

        HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(currentPlayer);;


		// find out which country the player wants to place in
		Territory toFill = currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, playersTerritories, null, false, RequestReason.PLACING_ARMIES_PHASE);
        
		// find out how many armies the player want to deploy there 
		int deployedAmount = currentPlayer.getCommunicationMethod()
				.getNumberOfArmies(currentPlayer, playersUndeployedArmies.size(), RequestReason.PLACING_ARMIES_PHASE, toFill, null);
		
		// do the deployment!
		Change stateChange = new ArmyPlacement(currentPlayer.getId(), toFill.getId(), deployedAmount, PLAYER_PLACING_ARMIES);
        applyAndReportChange(gameState, stateChange);

		return PLAYER_PLACING_ARMIES;
	}

	
	/**
	 * A function that carries out the whole attack - checks its validity,
	 * carries out a fight and applies the result of the battle.
	 * 
	 * @return
	 */
	protected PlayState invadeCountry() {
		
		// get the territories of the current player
		possibleAttackingTerritories = TerritoryUtils
                .getPossibleAttackingTerritories(gameState, currentPlayer);

        // if a player has no options // TODO: still ask them for the protocol
        if (possibleAttackingTerritories.size() == 0) {
            if (currentPlayer.getCommunicationMethod() instanceof DumbBotInterfaceProtocol) {
                currentPlayer.getCommunicationMethod().getTerritory(currentPlayer, possibleAttackingTerritories, null, true, RequestReason.ATTACK_CHOICE_FROM);
            }
            return PLAYER_MOVING_ARMIES;
        }

		// find out which country the player wants to attack from
		Territory attacking = currentPlayer.getCommunicationMethod()
				.getTerritory(currentPlayer, possibleAttackingTerritories, null, true, RequestReason.ATTACK_CHOICE_FROM);
		
		if(attacking == null){
			debug("PLAYER DOESNT WANT TO INVADE");
			return PLAYER_MOVING_ARMIES;
		}
		
		// get the enemy neighbours of the country
		HashSet<Territory> attackable = TerritoryUtils
				.getEnemyNeighbours(gameState, attacking, currentPlayer);
		
			
		// ask the player which country he wants to attack
		Territory defending = currentPlayer
				.getCommunicationMethod().getTerritory(currentPlayer, attackable, attacking, true, RequestReason.ATTACK_CHOICE_TO);
		
		if(defending == null){
			System.out.println("GE: def null");
			return PLAYER_MOVING_ARMIES;
		}

		
		// find out who owns this fated land
		Player defendingPlayer = PlayerUtils.getTerritoryOwner(gameState, defending);

		
		// work out the max number of armies that may attack
		// and how many may defend as per rules
		int attackingArmies = ArmyUtils
				.getNumberOfArmiesOnTerritory(currentPlayer, attacking);
		
		debug("attacking armies: " + attackingArmies);
		int maxAttackingDice = (attackingArmies > 3) ? 3 : attackingArmies - 1;
		
		int defendingArmies = ArmyUtils
				.getNumberOfArmiesOnTerritory(defendingPlayer, defending);
		int maxDefendingDice = defendingArmies > 2 ? 2 : defendingArmies;

		// ask the players how many they would like to use
		int attackDiceNumber = currentPlayer.
				getCommunicationMethod().getNumberOfDice(currentPlayer, maxAttackingDice, RequestReason.ATTACK_CHOICE_DICE, attacking, defending);
		int defendDiceNumber = defendingPlayer.
				getCommunicationMethod().getNumberOfDice(defendingPlayer, maxDefendingDice, RequestReason.DEFEND_CHOICE_DICE, attacking, defending);

		// create an object to represent the fight
		FightResult result = new FightResult(currentPlayer.getId(), defendingPlayer.getId(), 
				attacking.getId(), defending.getId());
	
		// decide the results of the fight
		currentPlayer.logAttack();
		arbitration.carryOutFight(result, attackDiceNumber, defendDiceNumber);

        applyAndReportChange(gameState, result);

		// if the attacking player won and they still have surplus armies,
		// give the option to move them
		if(result.getDefendersLoss() == defendingArmies){
			
			currentPlayerHasTakenCountry = true;

			if((attackingArmies - result.getAttackersLoss() - attackDiceNumber) > 1) {
                moveMoreArmies(result);
            }

            if(PlayerUtils.playerIsOut(defendingPlayer)){
                Change stateChange = new PlayerRemoval(currentPlayer.getId(), defendingPlayer.getId());
                applyAndReportChange(gameState, stateChange);
            }

            if(checkTheEndOfGame())
				return END_GAME;
	
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
		Territory attackingTerritory = gameState.lookUpTerritory(result.getAttackingTerritoryId());
		Territory defendingTerritory = gameState.lookUpTerritory(result.getDefendingTerritoryId());
		ArrayList<Army> remainingAttackArmies = ArmyUtils
				.getArmiesOnTerritory(currentPlayer, attackingTerritory);
		
		// let the player decide how many armies they want to move
		int movedAmount = currentPlayer.getCommunicationMethod()
				.getNumberOfArmies(currentPlayer, remainingAttackArmies.size() - 1, RequestReason.POST_ATTACK_MOVEMENT, defendingTerritory, attackingTerritory);
		
		if(movedAmount > 0){

				// add a new change to the state
            Change stateChange = new ArmyMovement(currentPlayer.getId(), result.getAttackingTerritoryId(),
                    result.getDefendingTerritoryId(), movedAmount, PLAYER_INVADING_COUNTRY);
            applyAndReportChange(gameState, stateChange);
		}
		
		
	}
	
	
	/**
	 * Called every time a player takes over a territory. The end of
	 * the game is recognised by checking the number of players.
	 * 
	 * @return
	 */
	private boolean checkTheEndOfGame(){
		if(winConditions.checkConditions(gameState)){
			System.out.println("END GAME!!!");
			return true;
		}
		else
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
	protected PlayState moveArmy() {
	
		// get a list of territories a player can deploy from
		//
        HashSet<Territory> canBeDeployedFrom = TerritoryUtils
				.getDeployable(gameState, currentPlayer);


        // if a player has no options
        if (canBeDeployedFrom.size() == 0) {
            if (currentPlayer.getCommunicationMethod() instanceof DumbBotInterfaceProtocol) {
                currentPlayer // let know the player interface  -- needed for protocol
                        .getCommunicationMethod().getTerritory(currentPlayer, canBeDeployedFrom, null, true, RequestReason.REINFORCEMENT_PHASE);

            }
            return endGo();
        }
		
		// find out which one the player wants to move from
		Territory source = currentPlayer
				.getCommunicationMethod().getTerritory(currentPlayer, canBeDeployedFrom, null, true, RequestReason.REINFORCEMENT_PHASE);
		
		//------------------------------------
		// HANDLE HERE NOT MOVING RESPONCE? - null country selection?
		if(source == null){
			debug("PLAYER DOESNT WANT TO MOVE");
			return endGo();
		}

		// get a list of territories a player can deploy too
		HashSet<Territory> canBeDeployedTo = TerritoryUtils
				.getFriendlyNeighbours(gameState, source, currentPlayer);

		// get the choice made
		Territory target = currentPlayer
				.getCommunicationMethod().getTerritory(currentPlayer, canBeDeployedTo, source, true, RequestReason.REINFORCEMENT_PHASE);

        if(target == null){
        	// they change their mind, let them decide again
            return PlayState.PLAYER_MOVING_ARMIES;
        }

        int numberOfArmiesThatMayBeMoved = ArmyUtils
				.getNumberOfMoveableArmies(currentPlayer, source);
		
		int movedAmount = currentPlayer.getCommunicationMethod()
				.getNumberOfArmies(currentPlayer, numberOfArmiesThatMayBeMoved, RequestReason.REINFORCEMENT_PHASE, target, source);
        

        Change stateChange = new ArmyMovement(currentPlayer.getId(), source.getId(), target.getId(), movedAmount, PLAYER_MOVING_ARMIES);
        applyAndReportChange(gameState, stateChange);

		return endGo();
	}

	
	/**
	 * Called every time a player ends their turn.
	 * 
	 * @return
	 */
	private PlayState endGo() {
		if (currentPlayerHasTakenCountry) {
            CardHandout c = new CardHandout(currentPlayer.getId());
            applyAndReportChange(gameState, c);

			currentPlayerHasTakenCountry = false;
			countryTakenInPreviousTurn = true;
		}
		
		currentPlayer = gameState.getPlayerQueue().next();
        applyAndReportChange(gameState, new PlayerChange(currentPlayer.getId()));
		
		return PLAYER_BEGINNING_TURN;
	}

}