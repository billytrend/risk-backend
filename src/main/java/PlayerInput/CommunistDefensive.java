package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.Results.Change;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

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
    public int getNumberOfDice(Player currentPlayer, int max, RequestReason attackChoiceDice, Territory attacking, Territory defending) {
		return max;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {

		// NEEDS TO BE FINISHED
        ArrayList<Territory> territoryArray = new ArrayList<Territory>(possibles);
		Random rand = new Random();

		// TODO: Make into helper method.
		for (int i = 0; i < territoryArray.size(); i++) {
			Player self = PlayerUtils.getTerritoryOwner(currentState, territoryArray.get(i));
            int strength = 0;
            if (self != null) {
                strength = ArmyUtils.getArmiesOnTerritory(self, territoryArray.get(i)).size();
            }

			if (strength < 3) {
				this.currentStrongTerritories.add(territoryArray.get(i));
			}

		}

		switch (reason) {

		case PLACING_ARMIES_SET_UP:

			// Randomly selects a territory from the list of possible choices.
			int randomNumber = rand.nextInt(territoryArray.size());
			return territoryArray.get(randomNumber);

		case PLACING_REMAINING_ARMIES_PHASE:

			// Chooses a territory to add 1 of the remaining armies to.
			// If there are still armies reset the counter and start from the
			// beginning again.

			Territory currentTerritory = territoryArray.get(this.initialDeploymentCounter);

			if (this.initialDeploymentCounter == territoryArray.size() - 1) {
				this.initialDeploymentCounter = 0;
			} else {
				this.initialDeploymentCounter++;
			}

			return currentTerritory;

		case PLACING_ARMIES_PHASE:

			// Chooses a territory to add 1 of the remaining armies to.
			// If there are still armies reset the counter and start from the
			// beginning again.

			Territory currentTerritoryPlacing = territoryArray.get(this.deploymentCounter);

			if (this.deploymentCounter == territoryArray.size() - 1) {
				this.deploymentCounter = 0;
			} else {
				this.deploymentCounter++;
			}

			return currentTerritoryPlacing;

		case ATTACK_CHOICE_FROM:
			return territoryArray.get(attackFromCounter);
			
			
		case ATTACK_CHOICE_TO:

			// Creates an arraylist of the weakest enemy territories (1 soldier)
			// Chooses a random one of these and returns it.
			ArrayList<Territory> weakestTerritories = new ArrayList<Territory>();

            double minArmies = Double.POSITIVE_INFINITY;
            Territory weakest = null;

			for (int i = 0; i < territoryArray.size(); i++) {
				Player enemyOwner = PlayerUtils.getTerritoryOwner(currentState,
						territoryArray.get(i));
				int numberOfEnemySoldiers = ArmyUtils
						.getNumberOfArmiesOnTerritory(enemyOwner,
								territoryArray.get(i));

                if (numberOfEnemySoldiers < minArmies) {
                    weakest = territoryArray.get(i);
                    minArmies = numberOfEnemySoldiers;
                }
			}

			return weakest;

		case REINFORCEMENT_PHASE:
			return territoryArray.get(0); // TODO: Figure out average and reinforce depending on
							// links.
		default:
			break;
		}

		return null;
	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {

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

	public void giveCard(Player player, Card card) {
		// TODO Auto-generated method stub

	}

	public Card getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player,
			ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void reportStateChange(Change change) {

    }

	@Override
	public void createResponse() {
		// TODO Auto-generated method stub
		
	}

}
