package GameState;

import java.util.ArrayList;
import java.util.HashMap;

public class RecreatedState extends State {
	
	public RecreatedState(ArrayList<String> playerIds, ArrayList<String> territoryIds, int startingArmies){
				
		super(new ArrayList<Player>());
		
		for(String id : playerIds){
			Player newPlayer = new Player(null, startingArmies, id);
			players.add(newPlayer);
			playerMapping.put(id, newPlayer);
		}
		
		for(String id : territoryIds){
			territoryMapping.put(id, new Territory(id));
		}
	}
	
}
