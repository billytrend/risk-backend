package PlayerInput;

import GameEngine.RequestReason;

import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.Results.Change;
import GameUtils.AIUtils;


import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by root on 08/04/2015.
 */

public class CommunistDefensive implements PlayerInterface{
    public State currentState;
    public Territory currentTer;


    public CommunistDefensive(State a){
        this.currentState = a;
    }


    /**
     * Always returns the maximum umber of dice.
     *
     * @param player The player
     * @param max Maximum number of dice to be returned
     * @return Number of dice to be rolled
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
        return max;
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
    public Territory getTerritory(Player player, HashSet<Territory> possibles,Territory from,
                                  boolean canResign, RequestReason reason) {

        //System.out.println(TerritoryUtils.getWeakestOwned(player, territoryList).getId());

        currentTer = from;

        switch (reason) {

            case PLACING_ARMIES_SET_UP:
                return AIUtils.getRandomTerritory(currentState, possibles);

            case PLACING_REMAINING_ARMIES_PHASE:
                return AIUtils.getWeakestTerritory(currentState,possibles);

            case PLACING_ARMIES_PHASE:
                return AIUtils.getWeakestTerritory(currentState,possibles);

            case ATTACK_CHOICE_FROM:
                currentTer = AIUtils.getStrongestTerritory(currentState,possibles);
                return currentTer;

            case ATTACK_CHOICE_TO:
                Territory weakestTer = AIUtils.getWeakestTerritory(currentState,possibles);
                if(AIUtils.goodIdea(currentState, currentTer, weakestTer, 1.25)){
                    return weakestTer;
                } else {
                    if(canResign){
                        return null;
                    }
                }
                return AIUtils.getRandomTerritory(currentState, possibles);

            case REINFORCEMENT_PHASE:
                if(canResign){
                    return null;
                }
            default:
                break;
        }

        return null;
    }


    /**
     * Returns a number of armies to move/attack etc
     *
     * @param player The player.
     * @param max Maximum number of dice to be rolled
     * @param reason The reason the method is being called.
     * @param to The territory attacking to
     * @param from The territory attacking from
     * @return
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        switch (reason) {
            case PLACING_ARMIES_SET_UP:
                return 1;
            case PLACING_REMAINING_ARMIES_PHASE:
                return 1;
            case PLACING_ARMIES_PHASE:
                return 1;
            case ATTACK_CHOICE_DICE:
                return max;
            case DEFEND_CHOICE_DICE:
                return 2;
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

    
    public void createResponse() {

    }


}
