package GameEngine;

import GameState.*;
import GameState.GameBuilders.DemoGame;
import GameState.StateUtils.ArmyUtils;
import GameState.StateUtils.MapUtils;
import GameState.StateUtils.OwnershipUtils;
import GameState.StateUtils.RuleUtils;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.TextInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static GameEngine.PlayStates.*;

public abstract class GameEngine {

	// armies assigned to the users at the beginning of the game
	static int armiesAtTheStart = 5;

	public static void play() {
		PlayStates playState = BEGINNING_STATE;
		State gameState = new State();

		while (true) {
			Player curPlayer = gameState.getPlayerQueue().getCurrent();

			switch (playState) {

				case BEGINNING_STATE:

					DemoGame.buildGame(gameState);

					Arbitration.setFirstPlayer(gameState);

					playState = FILLING_EMPTY_COUNTRIES;

					break;

				case FILLING_EMPTY_COUNTRIES:

					HashSet<Territory> emptyTerritories = MapUtils.getUnownedTerritories(gameState);

					ArrayList<Army> playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);

					CountrySelection toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, emptyTerritories, playersUndeployedArmies);

					OwnershipUtils.deployArmies(toFill.getCountry(), new ArrayList<Army>(playersUndeployedArmies.subList(0, 0)));

					if(!MapUtils.hasEmptyTerritories(gameState)) {
						playState = USING_REMAINING_ARMIES;
					}

					gameState.getPlayerQueue().next();

					break;

				case USING_REMAINING_ARMIES:

					HashSet<Territory> usersTerritories = MapUtils.getPlayersTerritories(curPlayer);

					playersUndeployedArmies = ArmyUtils.getUndeployedArmies(curPlayer);

					toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, usersTerritories, playersUndeployedArmies);

					OwnershipUtils.deployArmies(toFill.getCountry(), playersUndeployedArmies);

					if(!ArmyUtils.playerHasUndeployedArmies(gameState)) {
						playState = PLAYER_CONVERTING_CARDS;
					}

					gameState.getPlayerQueue().next();

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

					toFill = curPlayer.getInterfaceMethod().getTerritory(curPlayer, playersTerritories, playersUndeployedArmies);

					break;


				case PLAYER_INVADING_COUNTRY:
					break;

				case PLAYER_ENDED_GO:
					break;

				default:
					break;
			}
		}
	}

	/**
	 * Method which runs the main game loop.
	 */
	private static void playGame(){
		int a = 0;
		while(true){
			takeTurn(State.players.get(State.currentPlayer));
			System.out.println("State.State: " + State.endOfGame);
			if(State.endOfGame == true)
				break;
			State.nextPlayer();
		}
		System.out.println(State.players.get(State.currentPlayer).id + " WON ! \n" +
				"THE END.");
	}

	/**
	 * The players choose their territories and after all territories
	 * are assigned they place their remaining armies to the territories
	 * they choose.
	 */
	public static void startGame(){
		//"roll" for who goes first
		Random ran = new Random();
        State.currentPlayer = ran.nextInt(State.numOfPlayers);
        Territory territory;
        Player player;

        System.out.println("\nTaking territories....");
        while(!allTerritoriesOccupied()){
        	player = State.players.get(State.currentPlayer);
        	System.out.println("\n"  + player.id + "\n");

        	territory = State.map.territories.get(TextInterface.getTerritory(player) - 1);
        	if(territory.player == null){
        		placeArmies(player, 1, territory);
        		assignTerritory(player, territory);
        		State.nextPlayer();
        	}
        	else{
        		System.out.println("\nTHS TERRITORY IS ALREADY TAKEN\n");
        	}

        	State.print();
        }

        System.out.println("\nPlacing armies....");
        while(!allArmiesPlaced()){
        	player = State.players.get(State.currentPlayer);
        	System.out.println("\n"  + player.id);
        	System.out.println("State.Armies to place: " +
        			player.armiesToPlace + "\n");
        	territory = State.map.territories.get(TextInterface.getTerritory(player) - 1);
        	if(territory.player == player){
        		placeArmies(player, 1, territory);
        		State.nextPlayer();
        	}
        	else{
        		System.out.println("\nTHIS IS NOT YOUR TERRITORY\n");
        	}

        	State.print();
        }

	}


	/**
	 * Checks whether all territories are assigned a player
	 * @return
	 */
	private static boolean allTerritoriesOccupied(){
		for(int i = 0; i < State.map.territories.size(); i++){
			if(State.map.territories.get(i).player == null)
				return false;
		}
		return true;
	}

	/**
	 * Checks whether all players have places all the armies
	 * they were assigned.
	 *
	 * @return
	 */
	private static boolean allArmiesPlaced(){
		for(int i = 0; i < State.numOfPlayers; i++){
			if(State.players.get(i).armiesToPlace != 0)
				return false;
		}
		return true;
	}


	/**
	 * Conducting moves of the given player. Getting their
	 * move decisions, attacking and moving their units.
	 *
	 * @param player
	 */
	private static void takeTurn(Player player){

		// assigning and placing armies
		int assignedArmies = assignArmies(player);
		System.out.println("\n\nNEXT TURN\n"  + player.id);
		System.out.println("\nYou were just assigned " + assignedArmies + " armies.\n");
		if(assignedArmies > 0){
			System.out.println("Where would you like to place them?\n");
		}
		placeAssignedArmies(player);


		// player making decisions
		while(true){
			State.print();

			// keep asking for decision
			//until the user makes a valid choice
			int choice = -1;
			while((choice > 3) || (choice < 1)){
				choice = TextInterface.getInitialChoice();
			}

			// ATTACK decision
			if(choice == 1){
				// attackChoice[0] = from, attackChoice[1] = to
				int attackChoice[] = TextInterface.attackChoice();
				boolean success = AttackEngine.attack(State.map.territories.get((attackChoice[0] - 1)),
						State.map.territories.get((attackChoice[1] - 1)));
				if(success == false){
					System.out.println("\nINVALID ATTACK\n");
					continue;
				}
				if(State.endOfGame == true)
					break;
			}

			// MOVING ARMIES decision
			else if(choice == 2){
				// moveChoice[0] = from, moveChoice[1] = to, moveChoice[2] = num of units
				int moveChoice[] = TextInterface.moveChoice();
				if (!fortifyTerritories(State.players.get(State.currentPlayer),
						State.map.territories.get(moveChoice[0] - 1),
						State.map.territories.get(moveChoice[1] - 1),
						moveChoice[2]))
					System.out.println("\nINVALID MOVE.\n");
				else{
					// moving armies ends a turn
					return;
				}
			}

			// END THIS TURN decision
			else if(choice == 3){
				return;
			}
		}
	}


	/**
	 * Placing the armies assigned to the given player.
	 * All of the assigned armies need to be placed before
	 * the actual turn starts.
	 *
	 * @param player
	 */
	private static void placeAssignedArmies(Player player){
		Territory ter;
		int numOfArmies;

		// placing territories that were assigned to the player
		while(!playerOutOfArmies(player)){
			State.print();

			ter = State.map.territories.get(TextInterface.getTerritory(player) - 1);
			if(!player.territories.contains(ter)){
				System.out.println("\nTHIS IS NOT YOUR TERRITORY.");
				continue;
			}

			numOfArmies = TextInterface.getNumOfArmies(player);
			if(numOfArmies > player.armiesToPlace){
				System.out.println("\nNOT ENOUGH ARMIES.");
				continue;
			}

			placeArmies(player, numOfArmies, ter);
		}
	}

	/**
	 * Fortifying territories. Moving armies from one territory to another
	 * after making suitable checks.
	 *
	 * @param player
	 * @param from
	 * @param to
	 * @param numOfArmies
	 * @return
	 */
	private static boolean fortifyTerritories(Player player, Territory from,
			Territory to, int numOfArmies){

		// player needs to own both territories
		if((!player.territories.contains(from)) || (!player.territories.contains(to))){
			System.out.println("\nSPECIFY YOUR TERRITORIES!\n");
			return false;
		}
		// checking for number of armies and whether the territories are neighbours
		if(!checkMovingArmies(numOfArmies, from, to))
			return false;
		else
			moveArmy(numOfArmies, from, to);
		return true;
	}

	/**
	 * Used for placing armies on the map after they were assigned to the player.
	 * This method is not used for moving armies from one territory
	 * to another
	 *
	 * @param player
	 * @param numOfArmies
	 * @param territory
	 */
	public static void placeArmies(Player player, int numOfArmies, Territory territory){
		territory.armies.amount += numOfArmies;
		player.armiesToPlace -= numOfArmies;
	}


	/**
	 * Specifying how many armies should a user get
	 * depending on the amount of territories they own
	 *
	 * @param player
	 * @return
	 */
	static int assignArmies(Player player){
		//int numOfArmies = player.territories.size() / 3;
		int numOfArmies = player.territories.size() / 1; // for the purpose of simple game!
		player.armiesToPlace += numOfArmies;
		return numOfArmies;
		// plus continents!
	}


	/**
	 * Checks whether all players are out of armies
	 * @return
	 */
	static boolean playersOutOfArmies(){
		int numPlayers = State.players.size();
		for(int i = 0; i < numPlayers; i++){
			if(!playerOutOfArmies(State.players.get(i)))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether the given player is out of armies
	 * @param player
	 * @return
	 */
	static boolean playerOutOfArmies(Player player){
		if(player.armiesToPlace == 0)
			return true;
		else
			return false;
	}

	/**
	 * Moving the specified amount of armies from one territory to
	 * another. Does not perform any checks.
	 *
	 * @param numOfArmies
	 * @param from
	 * @param to
	 */
	static void moveArmy(int numOfArmies, Territory from, Territory to){
		from.armies.amount -= numOfArmies;
		to.armies.amount += numOfArmies;
	}

	/**
	 * Checks whether the specified number of armies can be moved from
	 * the specified 'from' territory and whether the given territories are
	 * neighbours
	 *
	 * @param numOfArmies
	 * @param from
	 * @param to
	 * @return
	 */
    static boolean checkMovingArmies(int numOfArmies, Territory from, Territory to){
    	if(!from.neighbours.contains(to)){
    		System.out.println("\nTHESE TERRITORIES ARE NOT NEIGHBOURS.\n");
    		return false;
    	}
    	if(from.armies.amount - 1 < numOfArmies){
    		System.out.println("\nYOU DON'T HAVE ENOUGH ARMIES.\n");
    		return false;
    	}
        return true;
    }

    /**
     * Assigning the given territory to the given player.
     * Adding it to the players list of territories.
     *
     * @param player
     * @param territory
     */
	static void assignTerritory(Player player, Territory territory){
		player.territories.add(territory);
		territory.player = player;
	}


	/**
	 * Removing the given territory from the list of given players territories
	 * @param player
	 * @param territory
	 */
	static void unassignTerritory(Player player, Territory territory){
		player.territories.remove(territory);
		if(territory.player == player)
			territory.player = null;
	}
}
