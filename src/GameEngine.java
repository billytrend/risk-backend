import java.util.ArrayList;
import java.util.Random;


public abstract class GameEngine {

	/// the first dice throw might be just generating one number
	
	public void setup(){
		GameState.numOfPlayers = UIEngine.getNumOfPlayers();
		ArrayList<Territory> territories = new ArrayList<Territory>();
		
		//add territories to map
		for(int i = 0; i<4; i++){
			Territory territory = new Territory();
			territories.add(territory);
		}
		GameState.map.territories = territories;
		
		//add neighbouring territories to each territory
		GameState.map.territories.get(1).neighbours.add(GameState.map.territories.get(2));
		GameState.map.territories.get(1).neighbours.add(GameState.map.territories.get(3));
		GameState.map.territories.get(2).neighbours.add(GameState.map.territories.get(1));
		GameState.map.territories.get(2).neighbours.add(GameState.map.territories.get(4));
		GameState.map.territories.get(3).neighbours.add(GameState.map.territories.get(1));
		GameState.map.territories.get(3).neighbours.add(GameState.map.territories.get(3));
		GameState.map.territories.get(4).neighbours.add(GameState.map.territories.get(2));
		GameState.map.territories.get(4).neighbours.add(GameState.map.territories.get(3));
	}
	public void startGame(){
		//"roll" for who goes first
		Random ran = new Random();
        GameState.currentPlayer = ran.nextInt(4);
        Territory territory;

        while(!allTerritoriesOccupied()){
        	territory = GameState.map.territories.get(UIEngine.getTerritory());
        	if(territory.player == null){
        		GameState.map.placeArmies(1, territory);
        		if(GameState.currentPlayer == GameState.numOfPlayers)
        			GameState.currentPlayer = 0;
        		else
        			GameState.currentPlayer ++;
        	}
        	
        }
        
        
	}
	// main game loop
	abstract void playGame();
	
	
	// once this is true you allow to place more armies
	private boolean allTerritoriesOccupied(){
		for(int i = 0; i < GameState.map.territories.size(); i++){
			if(GameState.map.territories.get(i).player == null)
				return false;
		}
		return true;
	}

	
    // TAKING TURN
	
	// differentiate between OUR move and other players move
	abstract void takeTurn(Player player);
	
	// let them move armies between territories
	// call move armies method
	abstract boolean fortifyTerritories(Player player, Territory from, Territory to, int numOfArmies);

}
