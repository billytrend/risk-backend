package PlayerInput;

import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.CardSelection;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;
import GameState.Card;
import GameState.Player;
import GameState.Territory;

import java.util.HashSet;

/**
 * Used to serve a real player, typing their decisions
 * in a console.
 *
 */
public class ConsoleInterface implements PlayerInterface {

	@Override
	public DiceSelection getNumberOfDice(Player player, int max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CountrySelection getTerritory(Player player,
										 HashSet<Territory> possibles, boolean canResign) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArmySelection getNumberOfArmies(Player player, int max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void giveCard(Player player, Card card) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public CardSelection getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
