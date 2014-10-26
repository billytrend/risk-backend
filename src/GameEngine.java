
public abstract class GameEngine {

	/// the first dice throw might be just generating one number

	// main game loop
	abstract void playGame();
	
	
	// once this is true you allow to place more armies
	abstract boolean allTerritoriesOccupied(Map map);
	

	
    // TAKING TURN
	
	// differentiate between OUR move and other players move
	abstract void takeTurn(Player player);
	
	// let them move armies between territories
	// call move armies method
	abstract boolean fortifyTerritories(Player player, Territory from, Territory to, int numOfArmies);


		
}
