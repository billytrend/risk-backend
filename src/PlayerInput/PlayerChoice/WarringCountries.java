package PlayerInput.PlayerChoice;
import GameState.Territory;

/**
 * Created by bt on 29/12/2014.
 */
public class WarringCountries {

    private Territory attacking;
    private Territory defending;

    public WarringCountries(Territory attacking, Territory defending) {
        this.attacking = attacking;
        this.defending = defending;
    }

    public Territory getAttacking() {
        return attacking;
    }

    public Territory getDefending() {
        return defending;
    }

}
