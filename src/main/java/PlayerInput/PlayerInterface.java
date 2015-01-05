package PlayerInput;

import GameState.StateClasses.Army;
import GameState.StateClasses.Card;
import GameState.StateClasses.Player;
import GameState.StateClasses.Territory;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;

public interface PlayerInterface {

    public DiceSelection getNumberOfDice(Player player, int max);

    public CountrySelection getTerritory(Player player, HashSet<Territory> possibles);

    public ArmySelection getNumberOfArmies(Player player, ArrayList<Army> playersUndeployedArmies);

    public void giveCard(Player player, Card card);

    public Object getCardOptions();

    }
