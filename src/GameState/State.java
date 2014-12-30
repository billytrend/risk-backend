package GameState;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;


public class State {

	private static SimpleGraph<Territory, Border> territories = new SimpleGraph<Territory, Border>();
	private static ArrayList<Player> players = new ArrayList<Player>();
	private static final PlayerQueue playerQueue = new PlayerQueue();

	public static Player getPlayer(int i) {
		return players.get(i);
	}

	public static void addPlayer(Player p) {
		State.players.add(p);
	}

	public static int countPlayers() {
		return players.size();
	}

	public static PlayerQueue getPlayerQueue() {
		return playerQueue;
	}

	public static int countTerritories() {
		return territories.vertexSet().size();
	}

	public static List<Territory> getNeighbours(Territory t) {
		return Graphs.neighborListOf(territories, t);
	}

	public static void addTerritory(Territory t) {
		territories.addVertex(t);
	}

	public static void addBorder(Territory a, Territory b) {
		territories.addEdge(a, b);
	}
}
