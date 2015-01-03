package PlayerInput;

import GameState.Army;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;
import PlayerInput.PlayerChoice.WarringCountries;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by bt on 29/12/2014.
 */
public class PlayerInterface {

    public DiceSelection getNumberOfDice(Player player, int max) {
        return null;
    }

    public WarringCountries getWarringTerritories(Player attacker, Player defender, int maxAttackDice, int maxDefendDice) {
        return null;
    }

    public CountrySelection getTerritory(Player player, HashSet<Territory> possibles, ArrayList<Army> playersUndeployedArmies) {
        return null;
    }

    public void giveCard(Player player, Card card) {

    }

    public Object getCardOptions() {
        return cardOptions;
    }
}
