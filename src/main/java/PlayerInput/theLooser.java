package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.Results.Change;
import GameUtils.TerritoryUtils;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by root on 08/04/2015.
 */
public class theLooser implements PlayerInterface {
    public State currentState;

    public theLooser(State a){
        this.currentState = a;
    }


    /**
     * *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
        return 1;
    }

    /**
     * The choice can be made only from the set of possible territories.
     *
     * @param player
     * @param possibles
     * @return
     */


    public Territory getTerritory(Player player,
                                  HashSet<Territory> possibles,Territory from, boolean canResign, RequestReason reason) {

        switch (reason) {

            case PLACING_ARMIES_SET_UP:
            case PLACING_REMAINING_ARMIES_PHASE:
            case PLACING_ARMIES_PHASE:
                return TerritoryUtils.getRandomTerritory(currentState, possibles);

            case ATTACK_CHOICE_FROM:
                return TerritoryUtils.getWeakestTerritory(currentState,possibles);

            case ATTACK_CHOICE_TO:
                return TerritoryUtils.getStrongestTerritory(currentState,possibles);

            case REINFORCEMENT_PHASE:
            default:
                return null;
        }
    }


    /**
     * The choice can only be made up to the specified max value.
     *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        switch (reason) {
            case PLACING_ARMIES_SET_UP:
                return 1;

            case PLACING_REMAINING_ARMIES_PHASE:
                return max;
            case PLACING_ARMIES_PHASE:
                return max;
            case ATTACK_CHOICE_DICE:
                return 1;
            case DEFEND_CHOICE_DICE:
                return 1;
            case REINFORCEMENT_PHASE:
                return 0; // TODO: Figure out average and reinforce depending on
            // links.
            case POST_ATTACK_MOVEMENT:
                return max; // Moves the maximum number of armies post attack.
		default:
			return 0;
        }
        

    }

    /**
     *
     * @return a triplet of cards which represents choice
     */
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        return possibleCombinations.get(0);
    }

    public void reportStateChange(Change change) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createResponse() {

    }

}
