package PlayerInput.PlayerChoice;

import GameState.StateClasses.Army;

import java.util.ArrayList;

/**
 * Created by bt on 30/12/2014.
 */
public class ArmySelection extends Choice {

    private final ArrayList<Army> armies;

    public ArmySelection(ArrayList<Army> armies) {
        this.armies = armies;

    }

    public ArrayList<Army> getArmies() {
        return armies;
    }
}
