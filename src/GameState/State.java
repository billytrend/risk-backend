package GameState;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.ArrayList;


public class State {

	SimpleGraph<Territory, Border> territories = new SimpleGraph<Territory, Border>(Border.class);

	private final ArrayList<Player> players;

	private final PlayerQueue playerQueue;

	ArrayList<Continent> continents = new ArrayList<Continent>();

	public State(ArrayList<Player> players) {
		this.players = players;
		this.playerQueue = new PlayerQueue(players);
	}

	public SimpleGraph<Territory, Border> getTerritories() {
		return territories;
	}

	public void setTerritories(SimpleGraph<Territory, Border> territories) {
		this.territories = territories;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public PlayerQueue getPlayerQueue() {
		return playerQueue;
	}

	public ArrayList<Continent> getContinents() {
		return continents;
	}

	public void setContinents(ArrayList<Continent> continents) {
		this.continents = continents;
	}
}
