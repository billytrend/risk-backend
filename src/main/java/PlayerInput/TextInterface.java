package PlayerInput;

import GameState.Army;
import GameState.Card;
import GameState.Territory;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class TextInterface implements PlayerInterface {

	protected void emit(GameState.Player p, String message) {
		System.out.print("[" + p.getId() + "]" + "\t\t");
		System.out.println(message);
	}

	/**
	 * Getting an integer from the user
	 * @return
	 */
	protected static int easyIn() {
		// ADDITIONAL CHECKS?
		int a;
		System.out.print("Please enter your selection: ");
		Scanner in = new Scanner(System.in);
		a = in.nextInt();
		return a;
	}

	public DiceSelection getNumberOfDice(GameState.Player player, int max) {
		emit(player, " how many dice do you want to throw?");
		int no = easyIn();
		return new DiceSelection(no);
	}


	public CountrySelection getTerritory(GameState.Player player, HashSet<Territory> possibles) {
		ArrayList<Territory> posList = new ArrayList<Territory>(possibles);
		emit(player, "Please choose a territory");
		for(int i = 0; i < possibles.size(); i++) {
			emit(player,  "\t" + i + ". " + posList.get(i).getId());
		}
//        int n = easyIn();
		return new CountrySelection(posList.get(0));
	}

	public ArmySelection getNumberOfArmies(GameState.Player player, ArrayList<Army> playersUndeployedArmies) {
		emit(player, "How many armies would you like to move? Max " + playersUndeployedArmies.size());
//        Integer no = easyIn();
		return new ArmySelection(playersUndeployedArmies);
	}

	public void giveCard(GameState.Player player, Card card) {

	}

	public Object getCardOptions() {
		return null;
	}

}
