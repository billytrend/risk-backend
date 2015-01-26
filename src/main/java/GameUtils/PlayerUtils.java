package GameUtils;

import GameState.*;

import java.util.ArrayList;
import java.util.HashSet;

public class PlayerUtils {

    /**
     *  *
     * @param state
     * @param player
     * @return
     */
    public static ArrayList<Continent> playerContinents(State state, Player player) {
        ArrayList<Continent> continents = new ArrayList<Continent>();
        HashSet<Territory> playersTerritories = TerritoryUtils.getPlayersTerritories(player);
        for (Continent c : state.getContinents()) {
            if (playersTerritories.containsAll(c.getTerritories())) {
                continents.add(c);
            }
        }
        return continents;
    }

    /**
     *
     * @param p
     * @return
     */
    public static int getNumberOfTerritoriesOwned(Player p) {
        return TerritoryUtils.getPlayersTerritories(p).size();
    }

    /**
     *
     * @param state
     * @param t
     * @return
     * */
    public static Player getTerritoryOwner(State state, Territory t) {
        for (Player p : state.getPlayers()) {
            if (TerritoryUtils.getPlayersTerritories(p).contains(t)) {
                return p;
            }
        }
        return null;
    }

    /**
     * *
     * @param state
     * @return
     */
     public static int countPlayers(State state) {
        return state.getPlayers().size();
     }

    /**
     * *
     * @param p
     * @return
     */
    public static boolean playerIsOut(Player p) {
    	System.out.println("\t\tNumber owned: " + getNumberOfTerritoriesOwned(p));
        return getNumberOfTerritoriesOwned(p) < 1;
    }
    
    
    public static boolean checkWin(Player player, State state){
		// if the player that just won has all the territories on map 
    	System.out.println("player owns: " + getNumberOfTerritoriesOwned(player));
    	System.out.println("num of terrs: " + TerritoryUtils.getAllTerritories(state).size());
		if(getNumberOfTerritoriesOwned(player) == TerritoryUtils.getAllTerritories(state).size())
			return true;
		else
			return false;
	}
    
}
