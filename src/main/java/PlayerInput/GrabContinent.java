package PlayerInput;

import GameEngine.RequestReason;

import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.AIUtils;
import GameUtils.ContinentUtils;
import GameUtils.Results.Change;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by Peter on 02/04/2015.
 */
public class GrabContinent implements PlayerInterface {
    public State currentState;
    public String continentID = "asia";


    public GrabContinent(State a, String continentID){
        this.currentState = a;
        this.continentID = continentID;
    }
    public GrabContinent() {

    }

    /**
     * *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
        return max;
    }

    /**
     * The choice can be made only from the set of possible territories.
     *
     * @param player
     * @param possibles
     * @return
     */


    public Territory getTerritory(Player player,
                                  HashSet<Territory> possibles,Territory from,boolean canResign, RequestReason reason) {

        ArrayList<Territory> territoryList = new ArrayList<Territory>(possibles);
        switch (reason) {

            case PLACING_ARMIES_SET_UP:
                return AIUtils.getContinentTerritory(currentState, territoryList, continentID);

            case PLACING_REMAINING_ARMIES_PHASE:
                return AIUtils.getContinentTerritory(currentState, territoryList, continentID);

            case PLACING_ARMIES_PHASE:
                return AIUtils.getContinentTerritory(currentState, territoryList, continentID);

            case ATTACK_CHOICE_FROM:
                return AIUtils.getStrongestTerritory(currentState, possibles);

            case ATTACK_CHOICE_TO:
                return AIUtils.getStrongestTerritory(currentState, possibles);

            case REINFORCEMENT_PHASE:
                return null;
            default:
                break;
        }

        return null;
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
                return 1;
            case PLACING_ARMIES_PHASE:
                return 1;
            case ATTACK_CHOICE_DICE:
                return max;
            case DEFEND_CHOICE_DICE:
                return max;
            case REINFORCEMENT_PHASE:
                return 0; // TODO: Figure out average and reinforce depending on
            // links.
            case POST_ATTACK_MOVEMENT:
                return max; // Moves the maximum number of armies post attack.
        }
        return 0;

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
        // TODO Auto-generated method stub

    }

}

