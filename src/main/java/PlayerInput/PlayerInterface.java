package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * An interface allowing for a game to be played
 * by AIs, locally by players, or over network.
 * A class that implements this interface provides
 * a specific way of communication with a player.
 *  
 */
public interface PlayerInterface {
    
    /**
     * * 
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason);

    /**
     * The choice can be made only from the set of possible territories.
     * 
     * @param player
     * @param possibles
     * @return
     */
    public Territory getTerritory(Player player,
    		HashSet<Territory> possibles,boolean canResign, RequestReason reason);

    /**
     * The choice can only be made up to the specified max value.
     *
     * @param player
     * @param max
     * @return
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from);
    
    /**
     *
     * @return a triplet of cards which represents choice
     */
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations);
    
    public void reportStateChange(Change change);
    
}
