import java.util.ArrayList;
import java.util.Random;


public abstract class GameEngine {

	// armies assigned to the users at the beginning of the game
	static int armiesAtTheStart = 5;

	public static void main(String[] argvs){
		setup();
		startGame();
		playGame();
	}
	
	/**
	 * Method which runs the main game loop.
	 */
	private static void playGame(){
		int a = 0;
		while(true){
			takeTurn(GameState.players.get(GameState.currentPlayer));
			System.out.println("GameState: " + GameState.endOfGame);
			if(GameState.endOfGame == true)
				break;
			GameState.nextPlayer();
		}
		System.out.println(GameState.players.get(GameState.currentPlayer).id + " WON ! \n" +
				"THE END.");
	}
	
	/**
	 * Setting the game up. Getting the number of players, creating map 
	 * and all the territories on it. Making all the necessary connections.
	 * 
	 */
	private static void setup(){
		// creating players
		GameState.numOfPlayers = UIEngine.getNumOfPlayers();
		GameState.players = new ArrayList<Player>();
		for(int i = 0; i < GameState.numOfPlayers; i++){
			GameState.players.add(new Player(armiesAtTheStart, "Player " 
						+ (i + 1)));
		}
		
		// creating territories
		ArrayList<Territory> territories = new ArrayList<Territory>();
		// adding territories to map
		for(int i = 0; i<4; i++){
			Territory territory = new Territory();
			territories.add(territory);
		}
		
		GameState.map = new Map(territories);

		//add neighbouring territories to each territory
		GameState.map.territories.get(0).neighbours.add(GameState.map.territories.get(1));
		GameState.map.territories.get(0).neighbours.add(GameState.map.territories.get(2));
		GameState.map.territories.get(1).neighbours.add(GameState.map.territories.get(0));
		GameState.map.territories.get(1).neighbours.add(GameState.map.territories.get(3));
		GameState.map.territories.get(2).neighbours.add(GameState.map.territories.get(0));
		GameState.map.territories.get(2).neighbours.add(GameState.map.territories.get(3));
		GameState.map.territories.get(3).neighbours.add(GameState.map.territories.get(1));
		GameState.map.territories.get(3).neighbours.add(GameState.map.territories.get(2));
		
		GameState.print();
	}

	/**
	 * The players choose their territories and after all territories
	 * are assigned they place their remaining armies to the territories
	 * they choose.
	 */
	public static void startGame(){
		//"roll" for who goes first
		Random ran = new Random();
        GameState.currentPlayer = ran.nextInt(GameState.numOfPlayers);
        Territory territory;
        Player player;

        System.out.println("\nTaking territories....");
        while(!allTerritoriesOccupied()){
        	player = GameState.players.get(GameState.currentPlayer);
        	System.out.println("\n"  + player.id + "\n");
        	
        	territory = GameState.map.territories.get(UIEngine.getTerritory(player) - 1);
        	if(territory.player == null){
        		placeArmies(player, 1, territory);
        		assignTerritory(player, territory);
        		GameState.nextPlayer();
        	}
        	else{
        		System.out.println("\nTHS TERRITORY IS ALREADY TAKEN\n");
        	}
        
        	GameState.print();
        }
         
        System.out.println("\nPlacing armies....");
        while(!allArmiesPlaced()){
        	player = GameState.players.get(GameState.currentPlayer);
        	System.out.println("\n"  + player.id);
        	System.out.println("Armies to place: " + 
        			player.armiesToPlace + "\n");
        	territory = GameState.map.territories.get(UIEngine.getTerritory(player) - 1);
        	if(territory.player == player){
        		placeArmies(player, 1, territory);
        		GameState.nextPlayer();
        	}
        	else{
        		System.out.println("\nTHIS IS NOT YOUR TERRITORY\n");
        	}
        
        	GameState.print();
        }
        
	}

	
	/**
	 * Checks whether all territories are assigned a player
	 * @return
	 */
	private static boolean allTerritoriesOccupied(){
		for(int i = 0; i < GameState.map.territories.size(); i++){
			if(GameState.map.territories.get(i).player == null)
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
		for(int i = 0; i < GameState.numOfPlayers; i++){
			if(GameState.players.get(i).armiesToPlace != 0)
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
			GameState.print();
			
			// keep asking for decision
			//until the user makes a valid choice
			int choice = -1;
			while((choice > 3) || (choice < 1)){
				choice = UIEngine.getInitialChoice();
			}
			
			// ATTACK decision
			if(choice == 1){
				// attackChoice[0] = from, attackChoice[1] = to
				int attackChoice[] = UIEngine.attackChoice();
				boolean success = AttackEngine.attack(GameState.map.territories.get((attackChoice[0] - 1)),
						GameState.map.territories.get((attackChoice[1] - 1)));
				if(success == false){
					System.out.println("\nINVALID ATTACK\n");
					continue;
				}
				if(GameState.endOfGame == true)
					break;
			}
			
			// MOVING ARMIES decision
			else if(choice == 2){
				// moveChoice[0] = from, moveChoice[1] = to, moveChoice[2] = num of units
				int moveChoice[] = UIEngine.moveChoice();
				if (!fortifyTerritories(GameState.players.get(GameState.currentPlayer),
						GameState.map.territories.get(moveChoice[0] - 1),
						GameState.map.territories.get(moveChoice[1] - 1),
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
			GameState.print();
			
			ter = GameState.map.territories.get(UIEngine.getTerritory(player) - 1);
			if(!player.territories.contains(ter)){
				System.out.println("\nTHIS IS NOT YOUR TERRITORY.");
				continue;
			}
			
			numOfArmies = UIEngine.getNumOfArmies(player);
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
		int numPlayers = GameState.players.size();
		for(int i = 0; i < numPlayers; i++){
			if(!playerOutOfArmies(GameState.players.get(i)))
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
