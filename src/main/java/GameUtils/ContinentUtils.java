package GameUtils;

import java.util.HashSet;

import GameState.Continent;
import GameState.Player;
import GameState.State;
import GameState.Territory;

public class ContinentUtils {
	
	public static void addContinent(State state, Territory[] territories, int reward, String id){
		HashSet<Territory> cont = new HashSet<Territory>();
		for (Territory t : territories){
			cont.add(t);
		}
		Continent continent = new Continent(cont, reward, id);
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

}
