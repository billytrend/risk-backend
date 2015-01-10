package GameState;

import java.util.HashSet;

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
