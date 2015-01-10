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
}
