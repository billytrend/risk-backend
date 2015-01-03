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
import java.util.Scanner;

public class TextInterface extends PlayerInterface {
	// should check whether your attacker/defender and
	// get the num of dice accordingly

	private void emit(Player p, String message) {
		System.out.print("[" + p.getId() + "]" + "\t\t");
		System.out.println(message);
	}

	public DiceSelection getNumberOfDice(Player player, int max) {
		emit(player, " how many dice do you want to throw?");
		int no = easyIn();
		return new DiceSelection(no);
	}

	public CountrySelection getTerritory(Player player, HashSet<Territory> possibles, ArrayList<Army> playersUndeployedArmies) {
		emit(player, "Please choose a territory");
		for(int i = 0; i < possibles.size(); i++) {
			emit(player,  "\t" + i + ". " + possibles.get(i));
		}
		int n = easyIn();
		return new CountrySelection(possibles.get(n));
	}

	public ArmySelection getNAttackers(Player player, int max) {
		emit(player, "How many attacker dice would you like to roll?");
		int no = easyIn();
		return new ArmySelection(no);
	}

	public ArmySelection getNDefenders(Player player, int max) {
		emit(player, "How many defender dice would you like to roll?");
		int no = easyIn();
		return new ArmySelection(no);
	}

	public void giveCard(Player player, Card card) {

	}

	/**
	 * Getting an integer from the user
	 * @return
	 */
	private static int easyIn() {
		// ADDITIONAL CHECKS?
		int a;
		System.out.print("Please enter your selection: ");
		Scanner in = new Scanner(System.in);
		a = in.nextInt();
		return a;
	}

}
