package PlayerInput;

import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.CardSelection;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;
import GameState.Card;
import GameState.Player;
import GameState.Territory;

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
    
    /**
     * * 
     * @param player
     * @param max
     * @return
     */
    public DiceSelection getNumberOfDice(Player player, int max);

    /**
     * The choice can be made only from the set of possible territories.
     * 
     * @param player
     * @param possibles
     * @return
     */
    public CountrySelection getTerritory(Player player,
    		HashSet<Territory> possibles,boolean canResign);

    /**
     * The choice can only be made up to the specified max value.
     *
     * @param player
     * @param max
     * @return
     */
    public ArmySelection getNumberOfArmies(Player player, int max);

    
    /**
     *
     * @param player
     * @param card
     */
    public void giveCard(Player player, Card card);

    
    /**
     *
     * * @return
     */
    public CardSelection getCardOptions();

}
