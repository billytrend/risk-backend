package GameState.Events;

/**
 * Created by bt on 30/12/2014.
 */
public class FightResult {

    private int defendersLoss;
    private int attackersLoss;
    private int[] attackDiceRolled;
    private int[] defendDiceRolled;

    public int getDefendersLoss() {
        return defendersLoss;
    }

    public void setDefendersLoss(int defendersLoss) {
        this.defendersLoss = defendersLoss;
    }

    public int getAttackersLoss() {
        return attackersLoss;
    }

    public void setAttackersLoss(int attackersLoss) {
        this.attackersLoss = attackersLoss;
    }

    public int[] getAttackDiceRolled() {
        return attackDiceRolled;
    }

    public void setAttackDiceRolled(int[] attackDiceRolled) {
        this.attackDiceRolled = attackDiceRolled;
    }

    public int[] getDefendDiceRolled() {
        return defendDiceRolled;
    }

    public void setDefendDiceRolled(int[] defendDiceRolled) {
        this.defendDiceRolled = defendDiceRolled;
    }

    public void addDefendLoss() {
        this.defendersLoss++;
    }

    public void addAttackLoss() {
        this.attackersLoss++;
    }

}
