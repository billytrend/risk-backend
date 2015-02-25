package GameState;

import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;

import static com.esotericsoftware.minlog.Log.debug;

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
	private PlayerQueue playerQueue;
	ArrayList<Continent> continents = new ArrayList<Continent>();
	private final ArrayList<Card> cards = new ArrayList<Card>();
	private int numberOfCardSetsUsed;
	
	public State(ArrayList<Player> players) {
		this.players = players;
		this.playerQueue = new PlayerQueue(players);
		numberOfCardSetsUsed = 0;
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

	public ArrayList<Card> getCards() {
		return cards;
	}
	
	public void setContinents(ArrayList<Continent> continents) {
		this.continents = continents;
	}

	public int getNumberOfCardSetsUsed() {
		return numberOfCardSetsUsed;
	}

	public void setNumberOfCardSetsUsed(int numberOfCardSetsUsed) {
		this.numberOfCardSetsUsed = numberOfCardSetsUsed;
	}

	/**
	 * Method used to print the current state of the game.
	 * Used for console versions only for the purpose of debugging.
	 * To be deleted in later ones.
	 */
	public void print(){
		debug("\n-----------------------------\n"
				+ "CURRENT STATE:");
		debug("Num of players: " + playerQueue.getNumberOfCurrentPlayers());
		
		Player p;
		for(Territory t : TerritoryUtils.getAllTerritories(this)){
			debug(t.getId());
			if(PlayerUtils.getTerritoryOwner(this, t) != null){
				p = PlayerUtils.getTerritoryOwner(this, t);
				debug("\t" + p.getId() + " terrs: " + PlayerUtils.getNumberOfTerritoriesOwned(p)
						+ "  undep: " + ArmyUtils.getUndeployedArmies(p));
				debug("\tarmies: " + ArmyUtils.getNumberOfArmiesOnTerritory(p, t));
				debug("\tneighbours: ");
				for(Territory n : TerritoryUtils.getNeighbours(this, t)){
					debug(" " + n.getId());
				}
			}
		}
		
	}
	
}
