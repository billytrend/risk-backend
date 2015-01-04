package GameState.StateUtils;

import GameState.*;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static HashSet<Territory> getNeighbours(State state, Territory territory) {
        return new HashSet<Territory>(Graphs.neighborListOf(state.getTerritories(), territory));
    }

    public static HashSet<Territory> getEnemyNeighbours(State state, Territory territory, Player player) {
        HashSet<Territory> neighbours = getNeighbours(state, territory);
        HashSet<Territory> playersTerritories = getPlayersTerritories(player);
        neighbours.removeAll(playersTerritories);
        return neighbours;
    }

    public static HashSet<Territory> getFriendlyNeighbours(State state, Territory territory, Player player) {
        HashSet<Territory> neighbours = getNeighbours(state, territory);
        HashSet<Territory> playersTerritories = getPlayersTerritories(player);
        neighbours.retainAll(playersTerritories);
        return neighbours;
    }

    public static HashSet<Territory> getTerritoriesWithMoreThanOneArmy(Player p) {
        HashSet<Territory> foundArmies = new HashSet<Territory>();
        HashSet<Territory> territoriesWithMoreThanOneArmy = new HashSet<Territory>();
        for (Territory t : getPlayersTerritories(p)) {
            if (foundArmies.contains(t)) {
                territoriesWithMoreThanOneArmy.add(t);
            } else {
                foundArmies.add(t);
            }
        }
        return territoriesWithMoreThanOneArmy;
    }

    public static void addTerritory(State state, Territory territory) {
        state.getTerritories().addVertex(territory);
    }

    public static void addBorder(State state, Territory a, Territory b) {
        state.getTerritories().addEdge(a, b);
    }
}
