package GameState;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;

import java.util.ArrayList;
import java.util.HashSet;


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

	
	
	public void print(){
		System.out.println("\n-----------------------------\n"
				+ "CURRENT STATE:");
		System.out.println("Num of players: " + playerQueue.getNumberOfCurrentPlayers());
		
		Player p;
		for(Territory t : TerritoryUtils.getAllTerritories(this)){
			System.out.println(t.getId());
			if(PlayerUtils.getTerritoryOwner(this, t) != null){
				p = PlayerUtils.getTerritoryOwner(this, t);
				System.out.println("\t" + p.getId() + " terrs: " + PlayerUtils.getNumberOfTerritoriesOwned(p)
						+ "  undep: " + ArmyUtils.getUndeployedArmies(p));
				System.out.println("\tarmies: " + ArmyUtils.getNumberOfArmiesOnTerritory(p, t));
				System.out.print("\tneighbours: ");
				for(Territory n : TerritoryUtils.getNeighbours(this, t)){
					System.out.print(" " + n.getId());
				}
				System.out.println();
			}
		}
		
	}
	
}
