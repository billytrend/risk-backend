package PlayerInput;

import GameEngine.RequestReason;

import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.Results.Change;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by root on 18/04/2015.
 */
public class SuperSwapper implements PlayerInterface{
    public int turnCounter = 0;
    public State currentState;
    public  int STARTUPMETRIC = 5;
    public String contID = "north_america";
    public Territory currTer;


    public SuperSwapper(State currentState) {
        this.currentState = currentState;
    }

    @Override
    public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
        return max;
    }


    /**
     * The choice can be made only from the set of possible territories.
     *
     * @param player
     * @param possibles
     * @param from
     * @param canResign
     * @param reason    @return
     */
    @Override
    public Territory getTerritory(Player player, HashSet<Territory> possibles, Territory from, boolean canResign, RequestReason reason) {
        PlayerInterface contGrab = new GrabContinent(currentState, contID);
        PlayerInterface bordCont = new BorderControl(currentState);
        PlayerInterface commie = new BorderControl(currentState);

        switch (reason) {
            case PLACING_ARMIES_SET_UP:
                if(turnCounter < STARTUPMETRIC) {
                    return contGrab.getTerritory(player,possibles,from,canResign,reason);
                } else {
                    return commie.getTerritory(player, possibles, from, canResign, reason);
                }


            case PLACING_REMAINING_ARMIES_PHASE:
                if(turnCounter < STARTUPMETRIC) {
                    return contGrab.getTerritory(player,possibles,from,canResign,reason);
                } else {
                    return commie.getTerritory(player, possibles, from, canResign, reason);
                }

            case PLACING_ARMIES_PHASE:
                if(turnCounter < STARTUPMETRIC) {
                    return contGrab.getTerritory(player,possibles,from,canResign,reason);
                } else {
                    return commie.getTerritory(player, possibles, from, canResign, reason);
                }

            case ATTACK_CHOICE_FROM:
                if(turnCounter < STARTUPMETRIC) {
                    currTer = contGrab.getTerritory(player,possibles,from,canResign,reason);
                    return currTer;
                } else {
                    currTer = commie.getTerritory(player, possibles, from, canResign, reason);
                    return currTer;
                }

            case ATTACK_CHOICE_TO:
                if(turnCounter < STARTUPMETRIC) {
                    return contGrab.getTerritory(player,possibles,from,canResign,reason);
                } else {
                    return commie.getTerritory(player, possibles, currTer, canResign, reason);
                }

            case REINFORCEMENT_PHASE:
                turnCounter++;
                if(turnCounter < STARTUPMETRIC) {
                    return bordCont.getTerritory(player,possibles,from,canResign,reason);
                }


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
     * @param reason
     * @param to
     * @param from   @return
     */
    @Override
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        PlayerInterface contGrab;
        switch (reason) {
            case PLACING_ARMIES_SET_UP:
                if(turnCounter < STARTUPMETRIC) {
                    contGrab = new GrabContinent(currentState, contID);
                    return contGrab.getNumberOfArmies(player, max, reason, to, from);
                } else {
                    return 1;
                }

            case PLACING_REMAINING_ARMIES_PHASE:
                if(turnCounter < STARTUPMETRIC) {
                    contGrab = new GrabContinent(currentState, contID);
                    return contGrab.getNumberOfArmies(player, max, reason, to, from);
                } else {
                    return 1;
                }

            case PLACING_ARMIES_PHASE:
                if(turnCounter < STARTUPMETRIC) {
                    contGrab = new GrabContinent(currentState, contID);
                    return contGrab.getNumberOfArmies(player, max, reason, to, from);
                } else {
                    return 1;
                }
            case ATTACK_CHOICE_DICE:
                return max;
            case DEFEND_CHOICE_DICE:
                return max;
            case REINFORCEMENT_PHASE:
                contGrab = new BorderControl(currentState);
                return contGrab.getNumberOfArmies(player, max, reason, to, from);

            // links.
            case POST_ATTACK_MOVEMENT:
                return max - 2; // Moves the maximum number of armies post attack. - 2
            default:
                return 0;
        }


    }

    /**
     * @param player
     * @param possibleCombinations
     * @return a triplet of cards which represents choice
     */
    @Override
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        return possibleCombinations.get(0);
    }

    @Override
    public void reportStateChange(Change change) {

    }

    @Override
    public void createResponse() {

    }
}
