package GameEngine.PlayerChoice;

/**
 * Contains the information about the number
 * of dice the player wants to throw.
 * 
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
