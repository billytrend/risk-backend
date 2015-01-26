package GameUtils;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import org.javatuples.Pair;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.HashSet;

public class TerritoryUtils {

    /**
     *  *
     * @param state
     * @return
     */
    public static HashSet<Territory> getAllTerritories(State state) {
        return new HashSet<Territory>(state.getTerritories().vertexSet());
    }
    
    public static HashSet<Pair<Territory, Territory>> getAllBorders(State state) {
        HashMap<Territory, Territory> borders = new HashMap<Territory, Territory>();
        for (Territory d : getAllTerritories(state)) {
            borders.add(
                    state.getTerritories().get
            )
            borders.add(new Pair<Territory, Territory>(DefaultEdge., d.getTarget()));
        }
    }

    /**
     *
     */
    public static boolean areNeighbours(State state, Territory a, Territory b) {
        return state.getTerritories().containsEdge(a, b);
    }

    /**
     *
     */
    public static HashSet<Territory> getUnownedTerritories(State state) {
        HashSet<Territory> allTerritories = getAllTerritories(state);
        for (Player p : state.getPlayers()) {
            allTerritories.removeAll(getPlayersTerritories(p));
        }
        return allTerritories;
    }

    /**
     *
     */
    public static HashSet<Territory> getPlayersTerritories(Player player) {
        HashSet<Territory> playersTerritories = new HashSet<Territory>();
        for (Army a : player.getArmies()) {
            if (a.getTerritory() != null) {
                playersTerritories.add(a.getTerritory());
            }
        }
        return playersTerritories;
    }

    /**
     *
     */
    public static boolean hasEmptyTerritories(State state) {
        return getUnownedTerritories(state).size() > 0;
    }

    /**
     *
     */
    public static HashSet<Territory> getNeighbours(State state, Territory territory) {
        return new HashSet<Territory>(Graphs.neighborListOf(state.getTerritories(), territory));
    }

    /**
     *
     */
    public static HashSet<Territory> getEnemyNeighbours(State state, Territory territory, Player player) {
        HashSet<Territory> neighbours = getNeighbours(state, territory);
        HashSet<Territory> playersTerritories = getPlayersTerritories(player);
        neighbours.removeAll(playersTerritories);
        return neighbours;
    }

    /**
     *
     */
    public static HashSet<Territory> getFriendlyNeighbours(State state, Territory territory, Player player) {
        HashSet<Territory> neighbours = getNeighbours(state, territory);
        HashSet<Territory> playersTerritories = getPlayersTerritories(player);
        neighbours.retainAll(playersTerritories);
        return neighbours;
    }

    /**
     *
     */
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

    /**
     *
     */
    public static void addTerritory(State state, Territory territory) {
        state.getTerritories().addVertex(territory);
    }

    /**
     *
     */
    public static void addBorder(State state, Territory a, Territory b) {
        state.getTerritories().addEdge(a, b);
    }

    /**
    *
    * * @param state
    * @return
    */
   public static int countTerritories(State state) {
       return getAllTerritories(state).size();
   }
}
