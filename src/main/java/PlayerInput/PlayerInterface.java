package PlayerInput;

import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerChoice.Choice;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public interface PlayerInterface {

    CountDownLatch waiter = null;
    
    public Choice getChoice();
    
    public Choice await();

    /**
     * *
     * @param ch
     */
    void setChoice(Choice ch);
    
    /**
     * * 
     * @param player
     * @param max
     * @return
     */
    public PlayerInterface getNumberOfDice(Player player, int max);

    /**
     * * 
     * @param player
     * @param possibles
     * @return
     */
    public PlayerInterface getTerritory(Player player, HashSet<Territory> possibles);

    /**
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
