package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.Results.Change;
import GameUtils.AIUtils;
import GameUtils.TerritoryUtils;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by root on 08/04/2015.
 */
public class TheLoser implements PlayerInterface {
    public State currentState;

    public TheLoser(State a){
        this.currentState = a;
    }


    public int getNumberOfDice(Player currentPlayer, int maxAttackingDice, RequestReason attackChoiceDice, Territory attacking, Territory defending) {
        return 1;
    }

    /**
     *
     * Returns a territory from a list of possibilities.
     * @param player The player
     * @param possibles Possible territories to choose from
     * @param from Territory attacking from (if required)
     * @param canResign Whether the user can resign at this point in time
     * @param reason Why the method is being called, influences the return type.
     * @return
     */
    public Territory getTerritory(Player player,
                                  HashSet<Territory> possibles,Territory from, boolean canResign, RequestReason reason) {

        switch (reason) {

            case PLACING_ARMIES_SET_UP:
            case PLACING_REMAINING_ARMIES_PHASE:
            case PLACING_ARMIES_PHASE:
                return AIUtils.getRandomTerritory(currentState, possibles);


            case ATTACK_CHOICE_FROM:
                return AIUtils.getWeakestTerritory(currentState,possibles);

            case ATTACK_CHOICE_TO:
                return AIUtils.getStrongestTerritory(currentState,possibles);

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

    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        return possibleCombinations.get(0);
    }

    public void reportStateChange(Change change) {
        // TODO Auto-generated method stub

    }

    
    public void createResponse() {

    }

}
