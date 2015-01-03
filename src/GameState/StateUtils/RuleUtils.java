package GameState.StateUtils;

import GameState.Army;
import GameState.Continent;
import GameState.Player;
import GameState.State;

import java.util.ArrayList;

/**
 * Created by bt on 03/01/2015.
 */
public class RuleUtils {

    public static void doArmyHandout(State state, Player p) {
        int totalHandout = 0;
        // hand out armies for the following reasons
        // 1. armies you have
        int n = OwnershipUtils.getNumberOfTerritories(p);
        totalHandout += n/3;
        // 2. continents you control
        ArrayList<Continent> continents = OwnershipUtils.playerContinents(state, p);
        for (Continent c : continents) {
            totalHandout +=  c.getArmyReward();
        }
        // TODO: 3. value of cards
        // TODO: 4.
        ArmyUtils.givePlayerNArmies(p, totalHandout);
    }

}
