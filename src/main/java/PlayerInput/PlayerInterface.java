package PlayerInput;

import GameState.Army;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

public interface PlayerInterface {

    /**
     * * 
     * @param player
     * @param max
     * @return
     */
    public DiceSelection getNumberOfDice(Player player, int max);

    /**
     * * 
     * @param player
     * @param possibles
     * @return
     */
    public CountrySelection getTerritory(Player player, HashSet<Territory> possibles);

    /**
     *
     * @param player
     * @param playersUndeployedArmies
     * @return
     */
    public ArmySelection getNumberOfArmies(Player player, ArrayList<Army> playersUndeployedArmies);

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
    public Object getCardOptions();

}
