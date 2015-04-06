package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.ArmyUtils;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by root on 02/04/2015.
 */
public class StartAustralia implements PlayerInterface {
    /**
     * *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason) {
        return 0;
    }

    /**
     * The choice can be made only from the set of possible territories.
     *
     * @param player
     * @param possibles
     * @return
     */


    public Territory getTerritory(Player player,
                                  HashSet<Territory> possibles,boolean canResign, RequestReason reason) {

        ArrayList<Territory> territoryList = new ArrayList<Territory>(possibles);

        switch (reason) {

            case PLACING_ARMIES_SET_UP:
                return getAustralianContinentTerritory(territoryList);

            case PLACING_REMAINING_ARMIES_PHASE:
                return getAustralianContinentTerritory(territoryList);

            case PLACING_ARMIES_PHASE:
                return getAustralianContinentTerritory(territoryList);

            case ATTACK_CHOICE_FROM:
                Territory strongest;
                int temp = 0;

                for(int i = 0; i < territoryList.size(); i++){

                    int numberOfArmies = ArmyUtils.getNumberOfArmiesOnTerritory(player,
                                    territoryList.get(i));

                   
                }

            case ATTACK_CHOICE_TO:
            case REINFORCEMENT_PHASE:
            default:
                break;
        }

        return null;
    }

    private Territory getAustralianContinentTerritory(ArrayList<Territory> territoryList){
        Random rand = new Random();

        for(int i = 0; i < territoryList.size(); i++){
            if(territoryList.get(i).getId() == "Indonesia" || territoryList.get(i).getId() == "New Guinea"
                    || territoryList.get(i).getId() == "Western Australia"
                    || territoryList.get(i).getId() == "Eastern Australia"){
                return territoryList.get(i);
            }
        }
        int randomNum = rand.nextInt((territoryList.size() - 0) + 1) + 0;
        return territoryList.get(randomNum);

    }

    /**
     * The choice can only be made up to the specified max value.
     *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        return 0;
    }

    /**
     *
     * @return a triplet of cards which represents choice
     */
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        return null;
    }

}

