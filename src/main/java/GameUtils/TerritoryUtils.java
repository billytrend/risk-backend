package GameUtils;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;

import org.javatuples.Pair;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class that contains the methods that have to do with any 'territory
 * management'
 *
 */
public class TerritoryUtils {

	/**
	 * @param state
	 * @return
	 */
	public static HashSet<Territory> getAllTerritories(State state) {
		return new HashSet<Territory>(state.getTerritories().vertexSet());
	}

	public static HashSet<Pair<Territory, Territory>> getAllBorders(State state) {
		HashSet<Pair<Territory, Territory>> borderPairs = new HashSet<Pair<Territory, Territory>>();

		for (DefaultEdge d : state.getTerritories().edgeSet()) {
			Territory src = state.getTerritories().getEdgeSource(d);
			Territory trg = state.getTerritories().getEdgeTarget(d);
			borderPairs.add(new Pair<Territory, Territory>(src, trg));
		}

		return borderPairs;
	}

	/**
	 * 
	 * @param state
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean areNeighbours(State state, Territory a, Territory b) {
		return state.getTerritories().containsEdge(a, b);
	}

	/**
	 * 
	 * @param state
	 * @return
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
	 * @param player
	 * @return
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
	 * @param state
	 * @return
	 */
	public static boolean hasEmptyTerritories(State state) {
		return getUnownedTerritories(state).size() > 0;
	}

	/**
	 * 
	 * @param state
	 * @param territory
	 * @return
	 */
	public static HashSet<Territory> getNeighbours(State state,
			Territory territory) {
		return new HashSet<Territory>(Graphs.neighborListOf(
				state.getTerritories(), territory));
	}

	/**
	 * 
	 * @param state
	 * @param territory
	 * @param player
	 * @return
	 */
	public static HashSet<Territory> getEnemyNeighbours(State state,
			Territory territory, Player player) {
		HashSet<Territory> neighbours = getNeighbours(state, territory);
		HashSet<Territory> playersTerritories = getPlayersTerritories(player);
		neighbours.removeAll(playersTerritories);
		return neighbours;
	}

	/**
	 * 
	 * @param state
	 * @param territory
	 * @param player
	 * @return
	 */
	public static HashSet<Territory> getFriendlyNeighbours(State state,
			Territory territory, Player player) {
		HashSet<Territory> neighbours = getNeighbours(state, territory);
		HashSet<Territory> playersTerritories = getPlayersTerritories(player);
		neighbours.retainAll(playersTerritories);
		return neighbours;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public static HashSet<Territory> getTerritoriesWithMoreThanOneArmy(Player p) {
		HashSet<Territory> territoriesWithMoreThanOneArmy = new HashSet<Territory>();
		for (Territory t : getPlayersTerritories(p)) {
			if (ArmyUtils.getArmiesOnTerritory(p, t).size() > 1)
				territoriesWithMoreThanOneArmy.add(t);
		}
		return territoriesWithMoreThanOneArmy;
	}

	/**
	 * 
	 * @param state
	 * @param p
	 * @return
	 */
	public static HashSet<Territory> getPossibleAttackingTerritories(
			State state, Player p) {
		HashSet<Territory> attackers = new HashSet<Territory>();
		for (Territory t : getTerritoriesWithMoreThanOneArmy(p)) {
			if (getEnemyNeighbours(state, t, p).size() > 0)
				attackers.add(t);
		}
		return attackers;
	}

	/**
	 * 
	 * @param state
	 * @param p
	 * @return
	 */
	public static HashSet<Territory> getDeployable(State state, Player p) {
		HashSet<Territory> deployable = new HashSet<Territory>();
		for (Territory t : getTerritoriesWithMoreThanOneArmy(p)) {
			if (getFriendlyNeighbours(state, t, p).size() > 0)
				deployable.add(t);
		}
		return deployable;
	}

	/**
	 * 
	 * @param state
	 * @param territory
	 */
	public static void addTerritory(State state, Territory territory) {
		state.getTerritories().addVertex(territory);
		state.addMapping(territory.getId(), territory);
	}

	public static void addBorder(State state, Territory a, Territory b) {
		state.getTerritories().addEdge(a, b);
	}

	public static Territory getTerritoryByName(State state, String name) {
		HashSet<Territory> allTerritories = getAllTerritories(state);
		for (Territory territory : allTerritories) {
			if (territory.getId().equals(name)) {
				return territory;
			}
		}
		return null;
	}

	public static ArrayList<String> getAllCountryNames(State state) {
		ArrayList<String> names = new ArrayList<String>();
		for (Territory t : getAllTerritories(state)) {
			names.add(t.getId());
		}
		return names;
	}

	public static ArrayList<String[]> getAllBorderPairs(State state) {
		ArrayList<String[]> pairs = new ArrayList<String[]>();
		for (Pair<Territory, Territory> p : getAllBorders(state)) {
			pairs.add(new String[] { p.getValue0().getId(),
					p.getValue1().getId() });
		}
		return pairs;
	}
	
	public static HashSet<Territory> getAllEnemyTerritories(State state, Player player){
		HashSet<Territory> territories = getAllTerritories(state);
		HashSet<Territory> friendlies = getPlayersTerritories(player);
		territories.removeAll(friendlies);
		return territories;
		
	}

}
