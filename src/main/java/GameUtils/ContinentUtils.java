package GameUtils;

import java.util.ArrayList;
import java.util.HashSet;

import GameState.Continent;
import GameState.Player;
import GameState.State;
import GameState.Territory;

public class ContinentUtils {
	
	public static void addContinent(State state, Territory[] territories, int reward, String id, int numeralId) {
		ArrayList<Territory> cont = new ArrayList<Territory>();
		for (Territory t : territories){
			cont.add(t);
		}
		Continent continent = new Continent(cont, reward, id, numeralId);
		state.getContinents().add(continent);
	}
	
	public static HashSet<Continent> getPlayersContinents(State state, Player player){
		HashSet<Continent> continentsOwned = new HashSet<Continent>();
		for(Continent c : state.getContinents()){
			if(checkPlayerOwnsContinent(player, c)) continentsOwned.add(c);
		}
		
		return continentsOwned;
	}
	public static boolean checkPlayerOwnsContinent(Player player, Continent continent){
		if (TerritoryUtils.getPlayersTerritories(player).containsAll(continent.getTerritories())) return true;		
		return false;
	}
	
	public static Continent getContinentById(State state, String id){
		ArrayList<Continent> continents = state.getContinents();
		for(Continent c : continents){
			if(c.getId() == id){
				return c;
			}
		}
		return null;
	}
	public static int getContinentPayout(State state, Player player){
		int contPayout = 0;
		HashSet<Continent> ownedConts = getPlayersContinents(state, player); 
		for(Continent c : ownedConts){
			contPayout += c.getArmyReward();
		}
		return contPayout;
	}

}
