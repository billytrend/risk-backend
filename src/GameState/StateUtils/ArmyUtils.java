package GameState.StateUtils;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;

import java.util.ArrayList;

/**
 * Created by bt on 03/01/2015.
 */
public class ArmyUtils {

    public static ArrayList<Army> getUndeployedArmies(Player player) {
        ArrayList<Army> undeployedArmies = new ArrayList<Army>();
        for (Army a : player.getArmies()) {
            if (a.getTerritory() == null) {
                undeployedArmies.add(a);
            }
        }
        return undeployedArmies;
    }

    public static boolean somePlayerHasUndeployedArmies(State state) {
        for (Player p : state.getPlayers()) {
            if (getUndeployedArmies(p).size() > 0) return true;
        }
        return false;
    }

    public static void givePlayerNArmies(Player p, int n) {
        for (int i = 0; i < n; i++) {
            p.getArmies().add(new Army());
        }
    }

    public static ArrayList<Army> getArmiesOnTerritory(Player p, Territory t) {
        ArrayList<Army> armies = new ArrayList<Army>();
        for (Army a : p.getArmies()) {
            if (a.getTerritory().equals(t)) {
                armies.add(a);
            }
        }
        return armies;
    }

    public static int getNumberOfArmiesOnTerritory(Player p, Territory t) {
        int count = 0;
        for (Army a : p.getArmies()) {
            if (a.getTerritory().equals(t)) {
                count++;
            }
        }
        return count;
    }

    public static void destroyArmies(Player p, Territory t, int n) {
        for (Army a : p.getArmies()) {
            if (a.getTerritory().equals(t)) {
                p.getArmies().remove(a);
            }
            if (--n == 0) {
                return;
            }
        }
    }

    public static void moveArmies(Territory target, ArrayList<Army> armies) {
        for(Army a : armies) {
            a.setTerritory(target);
        }
    }

}
