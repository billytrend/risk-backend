package PlayerInput;

import java.util.ArrayList;
import java.util.HashSet;

import org.javatuples.Triplet;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;

public class StrongestHunter implements PlayerInterface{

	public int getNumberOfDice(Player currentPlayer, int maxAttackingDice,
			RequestReason attackChoiceDice, Territory attacking,
			Territory defending) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			Territory from, boolean canResign, RequestReason reason) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumberOfArmies(Player player, int max, RequestReason reason,
			Territory to, Territory from) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Triplet<Card, Card, Card> getCardChoice(Player player,
			ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		// TODO Auto-generated method stub
		return null;
	}

	public void reportStateChange(Change change) {
		// TODO Auto-generated method stub
		
	}

	public void createResponse() {
		// TODO Auto-generated method stub
		
	}

}
