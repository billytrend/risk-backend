package GameUtils;

import java.util.*;

import GameState.Player;
import GameState.State;
import GameState.Territory;

public class AIUtils {
	  public static Territory getStrongestTerritory(State state, HashSet<Territory> territoryList){
	        Territory strongest = null;
	        int highestNumberOfSoldiers = 0;
	        for(Territory t:territoryList){
	        	Player player = PlayerUtils.getTerritoryOwner(state, t);
	            int numberOfArmies = ArmyUtils.getNumberOfArmiesOnTerritory(player,t);
	            if (numberOfArmies > highestNumberOfSoldiers) {
	            	highestNumberOfSoldiers = numberOfArmies;
	                strongest = t;
	            }
	        }

	        return strongest;
	    }


	    public static Territory getWeakestTerritory(State state, HashSet<Territory> territoryList){

	        int lowestNumberOfSoldiers = Integer.MAX_VALUE;
	        Territory weakest = null;
	        for (Territory t : territoryList) {
	            Player player = PlayerUtils.getTerritoryOwner(state,t);
	            int numberOfSoldiers = ArmyUtils
	                    .getNumberOfArmiesOnTerritory(player,t);



	            if (numberOfSoldiers < lowestNumberOfSoldiers) {
	            	lowestNumberOfSoldiers = numberOfSoldiers;
	                weakest = t;
	            }
	        }

	        return weakest;
	    }
	    public static boolean goodIdea(State state, Territory fromTer, Territory toTer, double ratioToOne){
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


	        if(armiesTo * ratioToOne >= armiesFrom)
	            flag = false;

	        return flag;
	    }
	    
	    public static ArrayList<HashSet<Territory>> orderClusters(
				ArrayList<HashSet<Territory>> clusters) {
			Collections.sort(clusters, new HashSetSizeComparator());
			return clusters;
		}
	    
	    public static ArrayList<HashSet<Territory>> getAllClusters(State state,
				Player player) {
			HashSet<Territory> world = TerritoryUtils.getPlayersTerritories(player);
			ArrayList<HashSet<Territory>> allClusters = new ArrayList<HashSet<Territory>>();
			HashSet<Territory> used = new HashSet<Territory>();
			for (Territory t : world) {
				HashSet<Territory> cluster = new HashSet<Territory>();
				// don't want to add the same cluster twice
				if (!used.contains(t)) {
					cluster.add(t);
					used.add(t);
					allClusters.add(findCluster(state, player, t, cluster, used));
				}
			}
			return orderClusters(allClusters);
		}

		public static HashSet<Territory> findCluster(State state, Player player,
			Territory territory, HashSet<Territory> cluster, HashSet<Territory> used) {
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
		
		public static Territory getRandomTerritory(State state,
				HashSet<Territory> possibles) {
			Random rand = new Random();
			int randomNumber = rand.nextInt(possibles.size());
			int count = 0;
			for (Territory territory : possibles) {
				if (count == randomNumber)
					return territory;
				count++;
			}
			return null;
		}

    public static Territory getContinentTerritory(State state, ArrayList<Territory> possibles, String continentID){
        Random rand = new Random();
        ArrayList<Territory> territoryList = ContinentUtils.getContinentById(state, continentID).getTerritories();
        int randomNum = rand.nextInt((territoryList.size()));

        for (int i = 0; i < possibles.size(); i++) {
            if(territoryList.get(randomNum).equals(possibles.get(i))){
                return territoryList.get(randomNum);
            }
        }

        randomNum = rand.nextInt(possibles.size());

        return possibles.get(randomNum);
    }

    public static String noTerritoryInContinent(State state, Player player, String[] continentIDs){
        for (int i = 0; i < continentIDs.length; i++) {
            if(!ownsOneInContinent(state, player, continentIDs[i])){
                return continentIDs[i];
            }

        }
        return null;
    }

    public static boolean ownsOneInContinent(State state, Player player, String continentID){
        HashSet<Territory> contTerritoryList = new HashSet<Territory>(ContinentUtils.getContinentById(state, continentID).getTerritories());
        HashSet<Territory> playerTerritoryList = TerritoryUtils.getPlayersTerritories(player);

        for (Territory t : playerTerritoryList) {
            if(contTerritoryList.contains(t)){
                return true;
            }
        }
        return false;
    }

}
