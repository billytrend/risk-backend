package PlayerInput;

import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;
import PlayerInput.PlayerChoice.WarringCountries;

import java.util.ArrayList;

/**
 * Created by bt on 29/12/2014.
 */
public interface PlayerInterface {

    public DiceSelection getNumberOfDice(Player player,int max);

    public WarringCountries getWarringTerritories(Player attacker, Player defender, int maxAttackDice, int maxDefendDice);

    public CountrySelection getTerritory(Player player, ArrayList<Territory> possibles);



}
