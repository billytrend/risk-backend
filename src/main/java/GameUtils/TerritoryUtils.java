package GameUtils;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;

import org.javatuples.Pair;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * A class that contains the methods that have to do with any 'territory
 * management'
 *
 */
public class TerritoryUtils {

    public static Territory getTerritoryWithName(State state, String name) {
        for (Territory t : getAllTerritories(state)) {
            if (t.getId().equals(name)) {
                return t;
            }
        }
        return null;
    }
    
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
	
    public static Territory getStrongestOwned(Player player, ArrayList<Territory> territoryList){
        Territory strongest;
        int temp = 0;
        int index = 0;
        for(int i = 0; i < territoryList.size(); i++){
            int numberOfArmies = ArmyUtils.getNumberOfArmiesOnTerritory(player,
                    territoryList.get(i));
            if (numberOfArmies > temp) {
                temp = numberOfArmies;
                index = i;
            }
        }

        return territoryList.get(index);
    }

    public static Territory getWeakestOwned(Player player, HashSet<Territory> territoryList){
        Territory strongest = null;
        int temp = Integer.MAX_VALUE;
        
        for(Territory territory: territoryList){
            int numberOfArmies = ArmyUtils.getNumberOfArmiesOnTerritory(player,
                    territory);
            if (numberOfArmies < temp) {
                temp = numberOfArmies;
                strongest = territory;
            }
        }

        return strongest;
    }

	  public static Territory getStrongestEnemy(State state, ArrayList<Territory> territoryList, String territoryID){

	        int temp = 0;
	        int index = 0;

	        for (int i = 0; i < territoryList.size(); i++) {
	            Player enemyOwner = PlayerUtils.getTerritoryOwner(state,
	                    territoryList.get(i));
	            int numberOfEnemySoldiers = ArmyUtils
	                    .getNumberOfArmiesOnTerritory(enemyOwner,
	                            territoryList.get(i));


	            if (numberOfEnemySoldiers > temp && (territoryList.get(i).getId() != territoryID)) {
	                temp = numberOfEnemySoldiers;
	                index = i;
	                }
	            }

	        return territoryList.get(index);
	        }

    public static Territory getWeakestEnemy(State state, ArrayList<Territory> territoryList, String territoryID){

        int temp = 0;
        int index = Integer.MAX_VALUE;

        for (int i = 0; i < territoryList.size(); i++) {
            Player enemyOwner = PlayerUtils.getTerritoryOwner(state,
                    territoryList.get(i));
            int numberOfEnemySoldiers = ArmyUtils
                    .getNumberOfArmiesOnTerritory(enemyOwner,
                            territoryList.get(i));


            if (numberOfEnemySoldiers < temp && (territoryList.get(i).getId() != territoryID)) {
                temp = numberOfEnemySoldiers;
                index = i;
            }
        }

        return territoryList.get(index);
    }

    public static boolean goodIdea(State state, Territory fromTer, Territory toTer){
        boolean flag = true;

        Player ownerFrom = PlayerUtils.getTerritoryOwner(state,
                fromTer);
        Player ownerTo = PlayerUtils.getTerritoryOwner(state,
                toTer);


        int armiesFrom = ArmyUtils
                .getNumberOfArmiesOnTerritory(ownerFrom,
                       fromTer);
        int armiesTo = ArmyUtils
                .getNumberOfArmiesOnTerritory(ownerTo,
                        toTer);


        if(armiesTo + (armiesTo / 4) >= armiesFrom)
            flag = false;

        return flag;
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

	/**
	 * 
	 * @param state
	 * @param a
	 * @param b
	 */
	public static void addBorder(State state, Territory a, Territory b) {
		state.getTerritories().addEdge(a, b);
	}

	public static ArrayList<HashSet<Territory>> getAllClusters(State state, Player player){
    	HashSet<Territory> world = getPlayersTerritories(player);
    	ArrayList<HashSet<Territory>> allClusters = new ArrayList<HashSet<Territory>>();
    	HashSet<Territory> used = new HashSet<Territory>();
    	for(Territory t: world){
    		HashSet<Territory> cluster = new HashSet<Territory>();
    		//don't want to add the same cluster twice
    		if(!used.contains(t)){
    			cluster.add(t);
    			used.add(t);
    			allClusters.add(findCluster(state,player, t, cluster, used));
    		}
    	}
    	return orderClusters(allClusters);
    }

	public static HashSet<Territory> findCluster(State state, Player player,
			Territory territory, HashSet<Territory> cluster, HashSet<Territory> used) {
		System.out.println("find cluster : length = " + cluster.size());
		HashSet<Territory> neighbours = TerritoryUtils.getFriendlyNeighbours(
				state, territory, player);
		for (Territory neighbour : neighbours) {
			if (!cluster.contains(neighbour)) {
				cluster.add(neighbour);
				used.add(neighbour);
				return findCluster(state, player, neighbour, cluster, used);
			}
		}
		return cluster;
	}
	public static ArrayList<HashSet<Territory>> orderClusters(ArrayList<HashSet<Territory>> clusters){
		Collections.sort(clusters, new HashSetSizeComparator());
    	return clusters;
    }
	public static Territory getRandomTerritory(State state, HashSet<Territory> possibles){
		Random rand = new Random();
		int randomNumber = rand.nextInt(possibles.size());
		int count = 0;
		for(Territory territory : possibles){
			if(count == randomNumber) return territory;
			count++;
		}
		return null;
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
            pairs.add(
                new String[] {
                        p.getValue0().getId(),
                        p.getValue1().getId()
                }
            );
        }
        return pairs;
    }

}
