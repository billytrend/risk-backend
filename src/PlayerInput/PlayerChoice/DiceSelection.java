package PlayerInput.PlayerChoice;

/**
 * Created by bt on 29/12/2014.
 */
public class DiceSelection extends Choice {

    private int numberOfDice;

    public DiceSelection(int n) {
        this.numberOfDice = n;
    }

    public int getNumberOfDice() {
        return this.numberOfDice;
    }

}
