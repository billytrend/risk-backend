package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Peter on 02/04/2015.
 */
public class StartAustralia implements PlayerInterface {
    public State currentState;

    public StartAustralia(State a){
        this.currentState = a;
    }


    /**
     * *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason) {
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
               return getStrongestOwned(player, territoryList);

            case ATTACK_CHOICE_TO:
                return getStrongestEnemy(territoryList);

            case REINFORCEMENT_PHASE:
                return null;
            default:
                break;
        }

        return null;
    }

    private Territory getStrongestEnemy(ArrayList<Territory> territoryList){

        int temp = 0;
        int index = 0;

        for (int i = 0; i < territoryList.size(); i++) {
            Player enemyOwner = PlayerUtils.getTerritoryOwner(currentState,
                    territoryList.get(i));
            int numberOfEnemySoldiers = ArmyUtils
                    .getNumberOfArmiesOnTerritory(enemyOwner,
                            territoryList.get(i));


            if (numberOfEnemySoldiers > temp && (territoryList.get(i).getId() != "Siam")) {
                temp = numberOfEnemySoldiers;
                index = i;
                }
            }

        return territoryList.get(index);
        }

    private Territory getStrongestOwned(Player player, ArrayList<Territory> territoryList){
        Territory strongest;
        int temp = 0;
        int index = 0;

        for(int i = 0; i < territoryList.size(); i++){

            int numberOfArmies = ArmyUtils.getNumberOfArmiesOnTerritory(player,
                    territoryList.get(i));

            if (numberOfArmies > temp) {
                temp = numberOfArmies;
                index = i;
            }
        }

        return territoryList.get(index);

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
        return null;
    }

}

