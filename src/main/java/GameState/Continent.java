package GameState;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class representing a continent along with its
 * territories and the army reward it provides.
 *
 */
public class Continent {

    private final ArrayList<Territory> territories;
    private final int armyReward;
    private final String id;

    public Continent(ArrayList<Territory> territories, int armyReward, String id) {
        this.territories = territories;
        this.armyReward = armyReward;
        this.id = id;
    }

    public ArrayList<Territory> getTerritories() {
        return territories;
    }

    public int getArmyReward() {
        return armyReward;
    }
    
    public String getId(){
    	return id;
    }
}
