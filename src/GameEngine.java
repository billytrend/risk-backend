import java.util.ArrayList;
import java.util.Random;


public abstract class GameEngine {

	/// the first dice throw might be just generating one number

	static int armiesAtTheStart = 5;
	
	public static void main(String[] argvs){
		setup();
		startGame();
		playGame();
	}
	
	// main game loop
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
		//add territories to map
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
	
	public static void startGame(){
		//"roll" for who goes first
		Random ran = new Random();
        GameState.currentPlayer = ran.nextInt(GameState.numOfPlayers);
        Territory territory;

        System.out.println("\nTaking territories....");
        while(!allTerritoriesOccupied()){
        	System.out.println("\n"  + GameState.players.get(GameState.currentPlayer).id + "\n");
        	
        	territory = GameState.map.territories.get(UIEngine.getTerritory() - 1);
        	if(territory.player == null){
        		placeArmies(GameState.players.get(GameState.currentPlayer),
        				1, territory);
        		assignTerritory(GameState.players.get(GameState.currentPlayer), territory);
        		GameState.nextPlayer();
        	}
        	else{
        		System.out.println("\nTHS TERRITORY IS ALREADY TAKEN\n");
        	}
        
        	GameState.print();
        }
         
        System.out.println("\nPlacing armies....");
        while(!allArmiesPlaced()){
        	System.out.println("\n"  + GameState.players.get(GameState.currentPlayer).id);
        	System.out.println("Armies to place: " + 
        			GameState.players.get(GameState.currentPlayer).armiesToPlace + "\n");
        	territory = GameState.map.territories.get(UIEngine.getTerritory() - 1);
        	if(territory.player == GameState.players.get(GameState.currentPlayer)){
        		placeArmies(GameState.players.get(GameState.currentPlayer), 1, territory);
        		GameState.nextPlayer();
        	}
        	else{
        		System.out.println("\nTHIS IS NOT YOUR TERRITORY\n");
        	}
        
        	GameState.print();
        }
        
	}

	
	// once this is true you allow to place more armies
	private static boolean allTerritoriesOccupied(){
		for(int i = 0; i < GameState.map.territories.size(); i++){
			if(GameState.map.territories.get(i).player == null)
				return false;
		}
		return true;
	}
	
	private static boolean allArmiesPlaced(){
		for(int i = 0; i < GameState.numOfPlayers; i++){
			if(GameState.players.get(i).armiesToPlace != 0)
				return false;
		}
		return true;
	}

		
    // TAKING TURN
	
	// differentiate between OUR move and other players move
	private static void takeTurn(Player player){
		int assignedArmies = assignArmies(player);
		Territory ter;
		int numOfArmies;
		System.out.println("\n\nNEXT TURN\n"  + GameState.players.get(GameState.currentPlayer).id);
		System.out.println("\nYou were just assigned " + assignedArmies + " armies.\n" +
				"Where would you like to place them?\n");
		
		while(!playerOutOfArmies(player)){
			GameState.print();
			
			ter = GameState.map.territories.get(UIEngine.getTerritory() - 1);
			if(!player.territories.contains(ter)){
				System.out.println("\nTHIS IS NOT YOUR TERRITORY.");
				continue;
			}
			numOfArmies = UIEngine.getNumOfArmies();
			if(numOfArmies > player.armiesToPlace){
				System.out.println("\nNOT ENOUGH ARMIES.");
				continue;
			}
			placeArmies(player, numOfArmies, ter);
		}
		
		while(true){
			GameState.print();
			int choice = -1;
			while((choice > 3) || (choice < 1)){
				choice = UIEngine.getInitialChoice();
			}
			if(choice == 1){
				// 0 - from, 1 - to
				int attackChoice[] = UIEngine.attackChoice();
				boolean success = AttackEngine.attack(GameState.players.get(GameState.currentPlayer),
						GameState.map.territories.get((attackChoice[0] - 1)),
						GameState.map.territories.get((attackChoice[1] - 1)));
				if(success == false){
					System.out.println("\nINVALID ATTACK\n");
					continue;
				}
				if(GameState.endOfGame == true)
					break;
			}
			else if(choice == 2){
				// 0 - from, 1 - to, 2 - num of units
				int moveChoice[] = UIEngine.moveChoice();
				if (!fortifyTerritories(GameState.players.get(GameState.currentPlayer),
						GameState.map.territories.get(moveChoice[0] - 1),
						GameState.map.territories.get(moveChoice[1] - 1),
						moveChoice[2]))
					System.out.println("\nINVALID MOVE.\n");
				else{
					return;
				}
			}
			else if(choice == 3){
				return;
			}
		}
	}
	
	// let them move armies between territories
	// call move armies method
	private static boolean fortifyTerritories(Player player, Territory from, Territory to, int numOfArmies){
		if(!player.territories.contains(from)){
			System.out.println("\nTHIS IS NOT YOUR TERRITORY.\n");
			return false;
		}
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

	// use for moves, placing armies - detect cheating
	//private Player checkOwnership(Territory territory){
	//	return territory.player;
	//}

	// number of territories is got from player
	// it gives armies to the player
	// do it several times at the beginning of the game
	static int assignArmies(Player player){
		//int numOfArmies = player.territories.size() / 3;
		int numOfArmies = player.territories.size() / 1; // for the purpose of simple game!
		player.armiesToPlace += numOfArmies;
		return numOfArmies;
		// plus continents!
	}
	
	// checks whether all all players are out of armies
	static boolean playersOutOfArmies(){
		int numPlayers = GameState.players.size();
		for(int i = 0; i < numPlayers; i++){
			if(!playerOutOfArmies(GameState.players.get(i)))
				return false;
		}
		return true;
	}
	
	// check whether the given player is out of armies
	static boolean playerOutOfArmies(Player player){
		if(player.armiesToPlace == 0)
			return true;
		else 
			return false;
	}
	
	// CHECKS?
	static void moveArmy(int numOfArmies, Territory from, Territory to){
		from.armies.amount -= numOfArmies;
		to.armies.amount += numOfArmies;
	}
	
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
	
	static void assignTerritory(Player player, Territory territory){
		player.territories.add(territory);
		territory.player = player;
	}

	static void unassignTerritory(Player player, Territory territory){
		player.territories.remove(territory);
		if(territory.player == player)
			territory.player = null;
	}
}
