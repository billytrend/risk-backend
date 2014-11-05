import java.util.ArrayList;


public abstract class Map{
	ArrayList<Territory> territories;
	Map(ArrayList<Territory> territories){
		this.territories = territories;
	}
	
	public void placeArmies(int numOfArmies, Territory territory){
		territory.armies.amount += numOfArmies;
	}

	// use for moves, placing armies - detect cheating
	//private Player checkOwnership(Territory territory){
	//	return territory.player;
	//}

	// number of territories is got from player
	// it gives armies to the player
	// do it several times at the beginning of the game
	int assignArmies(Player player){
		return player.territories.size() / 3;
		// plus continents!
	}
	
	// checks whether all all players are out of armies
	boolean playersOutOfArmies(){
		int numPlayers = GameState.players.size();
		for(int i = 0; i < numPlayers; i++){
			if(!playerOutOfArmies(GameState.players.get(i)))
				return false;
		}
		return true;
	}
	
	// check whether the given player is out of armies
	boolean playerOutOfArmies(Player player){
		if(player.armiesToPlace == 0)
			return true;
		else 
			return false;
	}
	
	// CHECKS?
	void moveArmy(int numOfArmies, Territory from, Territory to){
		from.armies.amount -= numOfArmies;
		to.armies.amount += numOfArmies;
	}
}
