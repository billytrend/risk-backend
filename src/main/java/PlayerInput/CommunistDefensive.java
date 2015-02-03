package PlayerInput;

import java.util.HashSet;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;

public class CommunistDefensive implements PlayerInterface {

	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		return max;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void giveCard(Player player, Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Card getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}


}
