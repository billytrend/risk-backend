package PlayerInput;

import GameEngine.PlayerChoice.Choice;
import GameState.Card;
import GameState.Player;
import GameState.Territory;

import java.util.HashSet;

public class ConsoleInterface implements PlayerInterface {

	@Override
	public Choice getChoice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Choice await() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChoice(Choice ch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PlayerInterface getNumberOfDice(Player player, int max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlayerInterface getTerritory(Player player,
			HashSet<Territory> possibles, boolean canResign) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlayerInterface getNumberOfArmies(Player player, int max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlayerInterface giveCard(Player player, Card card) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlayerInterface getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
