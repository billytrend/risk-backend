package GameState.StateUtils;

import GameState.*;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bt on 30/12/2014.
 */
public class MapUtils {

    public static HashSet<Territory> getAllTerritories(State state) {
        return new HashSet<Territory>(state.getTerritories().vertexSet());
    }

    public static boolean areNeighbours(State state, Territory a, Territory b) {
        return state.getTerritories().containsEdge(a, b);
    }

    public static HashSet<Territory> getUnownedTerritories(State state) {
        HashSet<Territory> allTerritories = getAllTerritories(state);
        for (Player p : state.getPlayers()) {
            allTerritories.removeAll(getPlayersTerritories(p));
        }
        return allTerritories;
    }

    public static HashSet<Territory> getPlayersTerritories(Player player) {
        HashSet<Territory> playersTerritories = new HashSet<Territory>();
        for (Army a : player.getArmies()) {
            if (a.getTerritory() != null) {
                playersTerritories.add(a.getTerritory());
            }
        }
        return playersTerritories;
    }

    public static boolean hasEmptyTerritories(State state) {
        return getUnownedTerritories(state).size() > 0;
    }
}
