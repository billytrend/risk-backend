package GameUtils;

import java.util.ArrayList;
import java.util.HashSet;

import GameState.Continent;
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

}
