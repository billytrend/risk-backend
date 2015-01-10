package GameUtils;

import GameUtils.Events.FightResult;
import GameState.Army;
import GameState.Continent;
import GameState.Player;
import GameState.State;

import java.util.ArrayList;

public class RuleUtils {

    /**
     * *
     * @param state
     * @param p
     */
    public static void doArmyHandout(State state, Player p) {
        int totalHandout = 0;
        // hand out armies for the following reasons
        // 1. armies you have
        int n = PlayerUtils.getNumberOfTerritoriesOwned(p);
        totalHandout += n/3;
        // 2. continents you control
        ArrayList<Continent> continents = PlayerUtils.playerContinents(state, p);
        for (Continent c : continents) {
            totalHandout +=  c.getArmyReward();
        }
        // TODO: 3. value of cards
        // TODO: 4.
        ArmyUtils.givePlayerNArmies(p, totalHandout);
    }


    /**
    *
    * @param res
    */
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