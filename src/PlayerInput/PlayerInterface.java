package PlayerInput;

import GameState.Army;
import GameState.Card;
import GameState.Territory;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by bt on 03/01/2015.
 */
public interface PlayerInterface {

    public DiceSelection getNumberOfDice(GameState.Player player, int max);

    public CountrySelection getTerritory(GameState.Player player, HashSet<Territory> possibles);

    public ArmySelection getNumberOfArmies(GameState.Player player, ArrayList<Army> playersUndeployedArmies);

    public void giveCard(GameState.Player player, Card card);

    public Object getCardOptions();

    }
