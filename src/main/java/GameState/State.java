package GameState;

import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;

/**
 * A class that represents a current state of the game including
 * the information about players, still queuing players (still in the game),
 * and all the continents. 
 *
 */
public class State {

	SimpleGraph<Territory, DefaultEdge> territories =
			new SimpleGraph<Territory, DefaultEdge>(DefaultEdge.class);
	
	private final ArrayList<Player> players;
	private final PlayerQueue playerQueue;
	ArrayList<Continent> continents = new ArrayList<Continent>();

	public State(ArrayList<Player> players) {
		this.players = players;
		this.playerQueue = new PlayerQueue(players);
	}

	public SimpleGraph<Territory, DefaultEdge> getTerritories() {
		return territories;
	}

	public void setTerritories(SimpleGraph<Territory, DefaultEdge> territories) {
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

	
	/**
	 * Method used to print the current state of the game.
	 * Used for console versions only for the purpose of debugging.
	 * To be deleted in later ones.
	 */
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
