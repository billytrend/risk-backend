import java.util.Scanner;

public class UIEngine {
	// should check whether your attacker/defender and
	// get the num of dice accordingly

	static int response() {
		return 0;
	}

	private static void printInitialChoices() {
		System.out.println("Please make a selection based on the following: ");
		System.out.println("    1.  Attack.");
		System.out.println("    2.  Move.");
	}

	private static int[] attackChoice() {
		int[] fromTo = new int[2];
		System.out.println("Which territory do you wish to attack from?");
		fromTo[0] = easyIn();
		System.out.println("Which territory do you wish to attack?");
		fromTo[1] = easyIn();
		return fromTo;
	}

	private static int[] moveChoice() {
		int[] fromTo = new int[3];
		System.out.println("Which territory do you wish to move from?");
		fromTo[0] = easyIn();
		System.out.println("Which territory do you wish to move to?");
		fromTo[1] = easyIn();
		System.out.println("How many armies do you wish to move?");
		fromTo[1] = easyIn();
		return fromTo;
	}

	private static int easyIn() {
		int a;

		System.out.print("Please enter your selection: ");

		Scanner in = new Scanner(System.in);
		a = in.nextInt();

		return a;
	}

	static int getNumOfDice(boolean attacker) {
		System.out.println("How many dice do you want?");
		return easyIn();

	}

	static int getTerritory() {
		System.out.println("Which territory do you want?");
		return easyIn();
	}

	static int getNumOfArmies() {
		System.out.println("How many armies do you want?");
		return easyIn();
	}

}
