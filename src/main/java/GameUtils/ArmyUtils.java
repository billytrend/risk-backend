package GameUtils;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;

import java.util.ArrayList;

public class ArmyUtils {

    /**
     *
     * @param player
     * @return
     */
    public static ArrayList<Army> getUndeployedArmies(Player player) {
        ArrayList<Army> undeployedArmies = new ArrayList<Army>();
        for (Army a : player.getArmies()) {
            if (a.getTerritory() == null) {
                undeployedArmies.add(a);
            }
        }
        return undeployedArmies;
    }

    /**
     *
     * @param state
     * @return
     */
    public static boolean somePlayerHasUndeployedArmies(State state) {
        for (Player p : state.getPlayers()) {
            if (getUndeployedArmies(p).size() > 0) return true;
        }
        return false;
    }

    /**
     * *
     * @param p
     * @param n
     */
    public static void givePlayerNArmies(Player p, int n) {
        for (int i = 0; i < n; i++) {
            p.getArmies().add(new Army());
        }
    }

    /**
     * *
     * @param p
     * @param t
     * @return
     */
    public static ArrayList<Army> getArmiesOnTerritory(Player p, Territory t) {
        ArrayList<Army> armies = new ArrayList<Army>();
        for (Army a : p.getArmies()) {
            if (a.getTerritory().equals(t)) {
                armies.add(a);
            }
        }
        return armies;
    }

    /**
     *
     * @param p
     * @param t
     * @return
     */
    public static int getNumberOfArmiesOnTerritory(Player p, Territory t) {
        int count = 0;
        for (Army a : p.getArmies()) {
            if (a.getTerritory().equals(t)) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param p
     * @param t
     * @param n
     */
    public static void destroyArmies(Player p, Territory t, int n) {
        ArrayList<Army> armies = p.getArmies();
        for (int i = 0; i < armies.size() && n > 0; i++) {
            if (armies.get(i).getTerritory().equals(t)) {
                p.getArmies().remove(i);
                n--;
            }
        }
    }

    /**
     *
     * @param target
     * @param armies
     */
    public static void moveArmies(Territory target, ArrayList<Army> armies) {
        for(Army a : armies) {
            a.setTerritory(target);
        }
    }

    /**
     * *
     * @param target
     * @param deployments
     */
    public static void deployArmies(Territory target, ArrayList<Army> deployments) {
        for (Army a : deployments) {
            a.setTerritory(target);
        }
    }
}
