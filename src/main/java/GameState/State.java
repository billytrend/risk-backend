package GameState;

import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.PlayerInterface;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.HashMap;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * A class that represents a current state of the game including
 * the information about players, still queuing players (still in the game),
 * and all the continents. 
 *
 */
public class State {

	protected SimpleGraph<Territory, DefaultEdge> territories = new SimpleGraph<Territory, DefaultEdge>(DefaultEdge.class);
    protected HashMap<String, Player> playerMapping = new HashMap<String, Player>();
	protected HashMap<String, Territory> territoryMapping = new HashMap<String, Territory>();
    protected HashMap<Integer, Player> playerNumberIdMapping = new HashMap<Integer, Player>();
	protected ArrayList<Player> players;
    protected ArrayList<PlayerInterface> ghosts = new ArrayList<PlayerInterface>();
	private PlayerQueue playerQueue;
	private ArrayList<Continent> continents = new ArrayList<Continent>();
	private final ArrayList<Card> cards = new ArrayList<Card>();
	private ArrayList<Player> endPositions = new ArrayList<Player>();
	
	public State(ArrayList<Player> players){
		this();
		setPlayers(players);
    }
    
    public State() {
    	players = new ArrayList<Player>();
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.playerQueue = new PlayerQueue(players);

    	playerMapping = new HashMap<String, Player>();
        for(Player player : players){
            playerMapping.put(player.getId(), player);
        }
    }
    
	public Player lookUpPlayer(String id){
		return playerMapping.get(id);
	}
	
	public Player lookUpPlayer(Integer id){
		return playerNumberIdMapping.get(id);
	}
	
	public Territory lookUpTerritory(String id){
		return territoryMapping.get(id);
	}
	
	
	public void addMapping(String id, Object obj){
		if(obj instanceof Territory){
			territoryMapping.put(id, (Territory) obj);
		}
		else if(obj instanceof Player){
			playerMapping.put(id, (Player) obj);
		}
	}
	
	public void addIntegerMapping(Integer id, Object obj){
		playerNumberIdMapping.put(id, (Player) obj);
	}
	
	public ArrayList<String> getPlayersIds(){
		ArrayList<String> ids = new ArrayList<String>();
		ids.addAll(playerMapping.keySet());
		return ids;
	}
	
	public ArrayList<String> getTerritoryIds(){
		ArrayList<String> ids = new ArrayList<String>();
		ids.addAll(territoryMapping.keySet());
		return ids;
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
//	public int getNumberOfCardSetsUsed() {
//		return numberOfCardSetsUsed;
//	}
//
//	public void setNumberOfCardSetsUsed(int numberOfCardSetsUsed) {
//		this.numberOfCardSetsUsed = numberOfCardSetsUsed;
//	}

    public void addGhost(PlayerInterface p) {
        this.ghosts.add(p);
    }
    
    public void setEndPosition(Player player){
    	endPositions.add(player);
    }
    public int getEndPosition(Player player){
    	for(int i = 0; i < endPositions.size(); i++){
    		if(player == endPositions.get(i)){
    			return players.size() -i;
    		}
    	}
    	return 0;
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

    public ArrayList<PlayerInterface> getGhosts() {
        return ghosts;
    }


}
