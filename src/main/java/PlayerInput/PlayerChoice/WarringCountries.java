package PlayerInput.PlayerChoice;
import GameState.StateClasses.Territory;

/**
 * Created by bt on 29/12/2014.
 */
public class WarringCountries {

    private Territory attacking;
    private Territory defending;
    private int nAttackDice;
    private int nDefendDice;

    public WarringCountries(Territory attacking, Territory defending, int nAttackDice, int nDefendDice) {
        this.attacking = attacking;
        this.defending = defending;
        this.nAttackDice = nAttackDice;
        this.nDefendDice = nDefendDice;
    }

    public Territory getAttacking() {
        return attacking;
    }

    public Territory getDefending() {
        return defending;
    }

    public int getnAttackDice() {
        return nAttackDice;
    }

    public int getnDefendDice() {
        return nDefendDice;
    }

}
