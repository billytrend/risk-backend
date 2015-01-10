package PlayerInput.PlayerChoice;

import GameState.Army;

import java.util.ArrayList;

public class ArmySelection extends Choice {

    private final ArrayList<Army> armies;

    public ArmySelection(ArrayList<Army> armies) {
        this.armies = armies;

    }

    public ArrayList<Army> getArmies() {
        return armies;
    }
}
