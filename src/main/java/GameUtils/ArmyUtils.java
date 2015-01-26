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
        	if(a.getTerritory() != null)
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
        ArrayList<Army> armies = getArmiesOnTerritory(p, t);
        for (int i = 0; i < n; i++) {
                p.getArmies().remove(armies.get(i));
        }
    }

    /**
     *
     * @param target
     * @param n
     */
    public static void moveArmies(Player sourceOwner, Territory source, Territory target, int n) {
        ArrayList<Army> moving = getArmiesOnTerritory(sourceOwner, source);
        Army a;
        for(int i = 0; i < n; i++) {
        	a = moving.get(i);
        	a.setTerritory(target);
        }
    }

    /**
     * *
     * @param target
     * @param n
     */
    public static void deployArmies(Player player, Territory target, int n) {
        ArrayList<Army> toDeploy = getUndeployedArmies(player);
        for (int i = 0; i < n; i++) {
           toDeploy.get(i).setTerritory(target);
        }
    }
    
    public static int getNumberOfMoveableArmies(Player p, Territory source) {
        return getNumberOfArmiesOnTerritory(p, source) - 1;
    }
}
