package PlayerInput;

import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameEngine.PlayerChoice.Choice;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

/**
 * An interface allowing for a game to be played
 * by AIs, locally by players, or over network.
 * A class that implements this interface provides
 * a specific way of communication with a player.
 *  
 */
public interface PlayerInterface {

    CountDownLatch waiter = null;
   
    public Choice getChoice();
    void setChoice(Choice ch);
   
    /**
     * 
     * @return
     */
    public Choice await();

    
    /**
     * * 
     * @param player
     * @param max
     * @return
     */
    public PlayerInterface getNumberOfDice(Player player, int max);

    /**
     * The choice can be made only from the set of possible territories.
     * 
     * @param player
     * @param possibles
     * @return
     */
    public PlayerInterface getTerritory(Player player,
    		HashSet<Territory> possibles,boolean canResign);

    /**
     * The choice can only be made up to the specified max value.
     *
     * @param player
     * @param max
     * @return
     */
    public PlayerInterface getNumberOfArmies(Player player, int max);

    
    /**
     *
     * @param player
     * @param card
     */
    public PlayerInterface giveCard(Player player, Card card);

    
    /**
     *
     * * @return
     */
    public PlayerInterface getCardOptions();

}
