package PlayerInput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;

public class CommunistDefensive implements PlayerInterface {

	public State currentState;
	private int initialDeploymentCounter;
	private int deploymentCounter;
	private int attackFromCounter;
	private ArrayList<Territory> currentStrongTerritories = new ArrayList<Territory>();

	private static final int MIN = 0;

	public CommunistDefensive(State a) {
		this.initialDeploymentCounter = 0;
		this.deploymentCounter = 0;
		this.currentState = a;
	}

	// Always returns the maximum number of dice.
	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		return max;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {

		// NEEDS TO BE FINISHED
		Territory[] territoryArray = (Territory[]) possibles.toArray();
		Random rand = new Random();

		// TODO: Make into helper method.
		for (int i = 0; i < territoryArray.length; i++) {
			Player self = PlayerUtils.getTerritoryOwner(currentState,
					territoryArray[i]);
			int strength = ArmyUtils.getArmiesOnTerritory(self,
					territoryArray[i]).size();

			if (strength < 3) {
				this.currentStrongTerritories.add(territoryArray[i]);
			}

		}

		switch (reason) {

		case PLACING_ARMIES_SET_UP:

			// Randomly selects a territory from the list of possible choices.
			int randomNumber = rand.nextInt(territoryArray.length - MIN + 1)
					+ MIN;
			return territoryArray[randomNumber];

		case PLACING_REMAINING_ARMIES_PHASE:

			// Chooses a territory to add 1 of the remaining armies to.
			// If there are still armies reset the counter and start from the
			// beginning again.

			Territory currentTerritory = territoryArray[this.initialDeploymentCounter];

			if (this.initialDeploymentCounter == territoryArray.length - 1) {
				this.initialDeploymentCounter = 0;
			} else {
				this.initialDeploymentCounter++;
			}

			return currentTerritory;

		case PLACING_ARMIES_PHASE:

			// Chooses a territory to add 1 of the remaining armies to.
			// If there are still armies reset the counter and start from the
			// beginning again.

			Territory currentTerritoryPlacing = territoryArray[this.deploymentCounter];

			if (this.deploymentCounter == territoryArray.length - 1) {
				this.deploymentCounter = 0;
			} else {
				this.deploymentCounter++;
			}

			return currentTerritoryPlacing;

		case ATTACK_CHOICE_FROM:
			return territoryArray[attackFromCounter];
			
			
		case ATTACK_CHOICE_TO:

			// Creates an arraylist of the weakest enemy territories (1 soldier)
			// Chooses a random one of these and returns it.
			ArrayList<Territory> weakestTerritories = new ArrayList<Territory>();

			for (int i = 0; i < territoryArray.length; i++) {
				Player enemyOwner = PlayerUtils.getTerritoryOwner(currentState,
						territoryArray[i]);
				int numberOfEnemySoldiers = ArmyUtils
						.getNumberOfArmiesOnTerritory(enemyOwner,
								territoryArray[i]);

				if (numberOfEnemySoldiers == 1) {
					weakestTerritories.add(territoryArray[i]);
				}
			}

			int randomWeakest = rand.nextInt(weakestTerritories.size() - MIN
					+ 1)
					+ MIN;
			return weakestTerritories.get(randomWeakest);

		case REINFORCEMENT_PHASE:
			return null; // TODO: Figure out average and reinforce depending on
							// links.
		default:
			break;
		}

		return null;
	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason) {

		switch (reason) {
		case PLACING_ARMIES_SET_UP:
			return 1;
			
		case PLACING_REMAINING_ARMIES_PHASE:
			return 1;
		case PLACING_ARMIES_PHASE:
			return 1;
		case ATTACK_CHOICE_DICE:
			return max;
		case DEFEND_CHOICE_DICE:
			return max;
		case REINFORCEMENT_PHASE:
			return 0; // TODO: Figure out average and reinforce depending on
						// links.
		case POST_ATTACK_MOVEMENT:
			return max; // Moves the maximum number of armies post attack.
		}

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