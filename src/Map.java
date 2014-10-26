
public abstract class Map{
	abstract void placeArmies(int numOfArmies, Territory territory);

	// use for moves, placing armies - detect cheating
	abstract Player checkOwnership(Territory territory);

	// number of territories is got from player
	// it gives armies to the player
	// do it several times at the beginning of the game
	abstract int assignArmies(Player player);
	
	// go to gamestate for the list of players
	abstract boolean playerOutOfArmies();
	
	abstract void moveArmy(int numOfArmies, Territory from, Territory to);

}
