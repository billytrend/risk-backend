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

public class CommunistAggressive implements PlayerInterface{
    public State currentState;
    public Territory currentTer;

    public CommunistAggressive(State a){
        this.currentState = a;
    }


	// Always returns the maximum number of dice.
	@Override
    public int getNumberOfDice(Player currentPlayer, int max, RequestReason attackChoiceDice, Territory attacking, Territory defending) {
		return max;
	}

	
	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {

		switch (reason) {
		case PLACING_ARMIES_SET_UP:
		case PLACING_REMAINING_ARMIES_PHASE:
		case PLACING_ARMIES_PHASE:
			return 1;
		case ATTACK_CHOICE_DICE:
		case DEFEND_CHOICE_DICE:
		case POST_ATTACK_MOVEMENT:
			return max;
		case REINFORCEMENT_PHASE:
			return 0; // TODO: Figure out average and reinforce depending on
						// links.
		default:
			return 0;
		}
	}

	public void getCard(Player player, Card card) {
		// TODO Auto-generated method stub

	}

	public Card getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * The choice can be made only from the set of possible territories.
     *
     * @param player
     * @param possibles
     * @return
     */


    public Territory getTerritory(Player player, HashSet<Territory> possibles,Territory from,
                                  boolean canResign, RequestReason reason) {

        //System.out.println(TerritoryUtils.getWeakestOwned(player, territoryList).getId());

        switch (reason) {

            case PLACING_ARMIES_SET_UP:
                return TerritoryUtils.getRandomTerritory(currentState, possibles);

            case PLACING_REMAINING_ARMIES_PHASE:
            case PLACING_ARMIES_PHASE:
                return TerritoryUtils.getWeakestOwned(player, possibles);

            case ATTACK_CHOICE_FROM:
                currentTer = TerritoryUtils.getStrongestOwned(player, possibles);
                return currentTer;

            case ATTACK_CHOICE_TO:
                Territory weakestTer = TerritoryUtils.getWeakestEnemy(currentState, possibles, currentTer.getId());
                if(TerritoryUtils.goodIdeaAgr(currentState, currentTer, weakestTer)){
                    return weakestTer;
                } else {
                    if(canResign){
                        return null;
                    }
                }
                return TerritoryUtils.getRandomTerritory(currentState, possibles);

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
