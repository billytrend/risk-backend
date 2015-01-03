package GameState.StateUtils;

import GameState.*;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by bt on 30/12/2014.
 */
public class OwnershipUtils {

    public static void deployArmies(Territory target, ArrayList<Army> deployments) {
        for (Army a : deployments) {
            a.setTerritory(target);
        }
    }

    public static ArrayList<Continent> playerContinents(State state, Player player) {
        ArrayList<Continent> continents = new ArrayList<Continent>();
        HashSet<Territory> playersTerritories = MapUtils.getPlayersTerritories(player);
        for (Continent c : state.getContinents()) {
           if (playersTerritories.containsAll(c.getTerritories())) {
                continents.add(c);
           }
        }
        return continents;
    }

    public static int getNumberOfTerritories(Player p) {
        return MapUtils.getPlayersTerritories(p).size();
    }
}
