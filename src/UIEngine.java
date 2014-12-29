import GameState.Player;

import java.util.Scanner;

public class UIEngine {
	// should check whether your attacker/defender and
	// get the num of dice accordingly
	
	/**
	 * Allowing the player to make choices during their turn.
	 * @return
	 */
	static int getInitialChoice() {
		System.out.println("Please make a selection based on the following: ");
		System.out.println("    1.  Attack.");
		System.out.println("    2.  Move.");
		System.out.println("    3.  Finish turn. ");
		return easyIn();
	}

	/**
	 * Getting details of the players decision to attack
	 * another player.
	 * @return
	 */
	static int[] attackChoice() {
		int[] fromTo = new int[2];
		System.out.println("\nWhich territory do you wish to attack from?");
		fromTo[0] = easyIn();
		System.out.println("Which territory do you wish to attack?");
		fromTo[1] = easyIn();
		return fromTo;
	}

	/**
	 * Getting details of users decision to move their armies.
	 * @return
	 */
	static int[] moveChoice() {
		int[] fromTo = new int[3];
		System.out.println("Which territory do you wish to move from?");
		fromTo[0] = easyIn();
		System.out.println("Which territory do you wish to move to?");
		fromTo[1] = easyIn();
		System.out.println("How many armies do you wish to move?");
		fromTo[2] = easyIn();
		return fromTo;
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

	/**
	 * Getting the number of dice the given players
	 * wants to throw.
	 * @param player
	 * @return
	 */
	static int getNumOfDice(Player player) {
		System.out.println(player.id + " how many dice do you want to throw?");
		return easyIn();

	}

	/**
	 * Getting the index of territory the given user wants
	 * to select.
	 * @param player
	 * @return
	 */
	static int getTerritory(Player player) {
		System.out.println(player.id + " which territory do you choose?");
		return easyIn();
	}
	
	
	/**
	 * Getting the number of armies the given user wants to move.
	 * @param player
	 * @return
	 */
	static int getNumOfArmies(Player player) {
		System.out.println(player.id + " how many armies do you want to move?");
		return easyIn();
	}

	/**
	 * Used at the start of the game to specify the number of users
	 * that are going to play. For non-network version of the game.
	 * @return
	 */
	public static int getNumOfPlayers() {
		System.out.println("How many players are going to play?");
		return easyIn();
	}

}
