package GameState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bt on 03/01/2015.
 */
public class Continent {

    private final HashSet<Territory> territories;
    private final int armyReward;

    public Continent(HashSet<Territory> territories, int armyReward) {
        this.territories = territories;
        this.armyReward = armyReward;
    }

    public HashSet<Territory> getTerritories() {
        return territories;
    }

    public int getArmyReward() {
        return armyReward;
    }
}
