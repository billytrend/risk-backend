package GameEngine;

import GameState.Events.ValidationResult;
import GameState.Player;
import GameState.State;
import GameState.Territory;

/**
 * Created by bt on 30/12/2014.
 */
public class Validation {

    /**
     * The method checks whether the given attack is valid and
     * returns false if it's not valid.
     *
     * @param attacker
     * @param defender
     * @return
     */
    private static ValidationResult checkAttack(Territory attacker, Territory defender){

        if (!State.areNeighbours(attacker, defender)) {
            return new ValidationResult(false, "TERRITORIES ARE NOT NEIGHBOURS");
        }

        else if (attacker.getOwner() != State.getPlayerQueue().getCurrent()) {
            return new ValidationResult(false, "CURRENT PLAYER DOESN'T OWN THIS TERRITORY!");
        }

        else if (attacker.countArmies() <= 1) {
            return new ValidationResult(false, "YOU DON'T HAVE ENOUGH ARMIES");
        }

        else if (defender.getOwner() != State.getPlayerQueue().getCurrent()) {
            return new ValidationResult(false, "CAN'T ATTACK OWN TERRITORY");
        }

        return new ValidationResult(true);

    }

}
