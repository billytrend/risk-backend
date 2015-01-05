package GameState.StateUtils;

import GameState.Events.FightResult;
import GameState.StateClasses.Army;
import GameState.StateClasses.Continent;
import GameState.StateClasses.Player;
import GameState.StateClasses.State;

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


    public static void applyFightResult(FightResult res) {
        ArmyUtils.destroyArmies(res.getAttacker(), res.getAttackingTerritory(), res.getAttackersLoss());
        ArmyUtils.destroyArmies(res.getDefender(), res.getDefendingTerritory(), res.getDefendersLoss());

        // if country defeated then move minimal armies across
        if (ArmyUtils.getNumberOfArmiesOnTerritory(res.getDefender(), res.getDefendingTerritory()) == 0) {
            ArrayList<Army> armiesToMove = new ArrayList<Army>(ArmyUtils.getArmiesOnTerritory(res.getAttacker(), res.getAttackingTerritory()).subList(0, res.getAttackDiceRolled().length));
            ArmyUtils.moveArmies(res.getDefendingTerritory(), armiesToMove);
        }
    }
}
