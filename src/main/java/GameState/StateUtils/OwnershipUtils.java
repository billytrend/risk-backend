package GameState.StateUtils;

import GameState.StateClasses.*;

import java.util.ArrayList;
import java.util.HashSet;

public class OwnershipUtils {

    /**
     * * 
     * @param target
     * @param deployments
     */
    public static void deployArmies(Territory target, ArrayList<Army> deployments) {
        for (Army a : deployments) {
            a.setTerritory(target);
        }
    }

    /**
     *  *
     * @param state
     * @param player
     * @return
     */
    public static ArrayList<Continent> playerContinents(State state, Player player) {
        ArrayList<Continent> continents = new ArrayList<Continent>();
        HashSet<Territory> playersTerritories = MapUtils.getPlayersTerritories(player);
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
    public static int getNumberOfTerritories(Player p) {
        return MapUtils.getPlayersTerritories(p).size();
    }

    /**
     *
     * @param state
     * @param t
     * @return
     * */
    public static Player getTerritoryOwner(State state, Territory t) {
        for (Player p : state.getPlayers()) {
            if (MapUtils.getPlayersTerritories(p).contains(t)) {
                return p;
            }
        }
        return null;
    }
}
