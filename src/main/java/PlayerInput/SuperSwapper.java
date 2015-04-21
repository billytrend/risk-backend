package PlayerInput;

import GameEngine.RequestReason;

import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.AIUtils;
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
    public int STARTUPMETRIC = 5;
    public int THREATTIMER = 7;
    public String contID = "south_america";
    public Territory currTer;


    public SuperSwapper(State currentState) {
        this.currentState = currentState;
    }

    /**
     * Always returns the maximum umber of dice.
     *
     * @param player The player
     * @param max Maximum number of dice to be returned
     * @return Number of dice to be rolled
     */
    @Override
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
                    return contGrab.getTerritory(player, possibles, from, canResign, reason);
                } else{
                    return commie.getTerritory(player, possibles, from, canResign, reason);
                }

            case PLACING_ARMIES_PHASE:
                if(turnCounter < STARTUPMETRIC) {
                    return contGrab.getTerritory(player,possibles,from,canResign,reason);
                } else if(turnCounter < THREATTIMER){
                    return AIUtils.getTerritoryWithStrongestNeighbour(currentState, possibles, player);
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
     * Returns a number of armies to move/attack etc
     *
     * @param player The player.
     * @param max Maximum number of dice to be rolled
     * @param reason The reason the method is being called.
     * @param to The territory attacking to
     * @param from The territory attacking from
     * @return
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
