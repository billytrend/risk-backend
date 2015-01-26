package GameState;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;


public class State {

	SimpleGraph territories = new SimpleGraph(DefaultEdge.class);

	private final ArrayList<Player> players;

	private final PlayerQueue playerQueue;

	ArrayList<Continent> continents = new ArrayList<Continent>();

	public State(ArrayList<Player> players) {
		this.players = players;
		this.playerQueue = new PlayerQueue(players);
	}

	public SimpleGraph getTerritories() {
		return territories;
	}

	public void setTerritories(SimpleGraph territories) {
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
