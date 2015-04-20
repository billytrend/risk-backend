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
public class Nailer implements PlayerInterface {
    public State currentState;
    public String[] contIDs = {"australia", "asia", "north_america", "south_america", "africa", "europe"};
    public Territory currTer;
    public Nailer(State a){
        this.currentState = a;
    }


    public int getNumberOfDice(Player currentPlayer, int maxAttackingDice, RequestReason attackChoiceDice, Territory attacking, Territory defending) {
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

        ArrayList<Territory> al = new ArrayList<Territory>(possibles);
        PlayerInterface swapper = new CommunistDefensive(currentState);

        switch (reason) {

            case PLACING_ARMIES_SET_UP:
                for(int i = 0; i < contIDs.length; i++){
                    String noTerritory = AIUtils.noTerritoryInContinent(currentState, player, contIDs);
                    if(noTerritory == null){
                        return AIUtils.getRandomTerritory(currentState, possibles);
                    } else {
                        return AIUtils.getContinentTerritory(currentState, al, noTerritory);
                    }

                }


            case PLACING_REMAINING_ARMIES_PHASE:
                return swapper.getTerritory(player, possibles, from, canResign, reason);

            case PLACING_ARMIES_PHASE:
                return swapper.getTerritory(player, possibles, from, canResign, reason);


            case ATTACK_CHOICE_FROM:
                currTer = swapper.getTerritory(player, possibles, from, canResign, reason);
                return currTer;

            case ATTACK_CHOICE_TO:
                return swapper.getTerritory(player, possibles, currTer, canResign, reason);

            case REINFORCEMENT_PHASE:
                swapper = new BorderControl(currentState);
                return swapper.getTerritory(player, possibles, from, canResign, reason);
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

        PlayerInterface swapper = new CommunistDefensive(currentState);
        switch (reason) {
            case PLACING_ARMIES_SET_UP:
                return 1;

            case PLACING_REMAINING_ARMIES_PHASE:
                return max;
            case PLACING_ARMIES_PHASE:
                return swapper.getNumberOfArmies(player,max, reason, to, from);
            case REINFORCEMENT_PHASE:
                swapper = new BorderControl(currentState);
                return swapper.getNumberOfArmies(player,max, reason, to, from);
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
