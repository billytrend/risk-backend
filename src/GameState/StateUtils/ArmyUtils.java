package GameState.StateUtils;

import GameState.Army;
import GameState.Player;
import GameState.State;

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

    public static boolean playerHasUndeployedArmies(State state) {
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

}
