package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ContinentUtils;
import GameUtils.Results.Change;
import GameUtils.TerritoryUtils;
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
    public StartAustralia() {

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
                return getAustralianContinentTerritory(currentState, territoryList);

            case PLACING_REMAINING_ARMIES_PHASE:
                return getAustralianContinentTerritory(currentState, territoryList);

            case PLACING_ARMIES_PHASE:
                return getAustralianContinentTerritory(currentState, territoryList);

            case ATTACK_CHOICE_FROM:
            	ArrayList<Territory> australia = ContinentUtils.getContinentById(currentState, "australia").getTerritories();
               return TerritoryUtils.getStrongestTerritory(currentState, possibles);

            case ATTACK_CHOICE_TO:
                return TerritoryUtils.getStrongestTerritory(currentState, possibles);

            case REINFORCEMENT_PHASE:
                return null;
            default:
                break;
        }

        return null;
    }





    private Territory getAustralianContinentTerritory(State state, ArrayList<Territory> possibles){
        Random rand = new Random();
        ArrayList<Territory> territoryList = ContinentUtils.getContinentById(state, "australia").getTerritories();
        int randomNum = rand.nextInt((territoryList.size()));

        for (int i = 0; i < possibles.size(); i++) {
             if(territoryList.get(randomNum).equals(possibles.get(i))){
                 return territoryList.get(randomNum);
             }
        }

        randomNum = rand.nextInt(possibles.size());

        return possibles.get(randomNum);
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


	@Override
	public void reportStateChange(Change change) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void createResponse() {
		// TODO Auto-generated method stub
		
	}

}

