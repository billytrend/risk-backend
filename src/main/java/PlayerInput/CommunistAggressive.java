package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.CardUtils;
import GameUtils.PlayerUtils;
import GameUtils.Results.Change;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class CommunistAggressive implements PlayerInterface {

	public State currentState;
	private int initialDeploymentCounter;
	private int deploymentCounter;
	private int attackFromCounter;
	private ArrayList<Territory> currentStrongTerritories = new ArrayList<Territory>();

	private static final int MIN = 0;

    public CommunistAggressive() {
        this.initialDeploymentCounter = 0;
        this.deploymentCounter = 0;
    }

	public CommunistAggressive(State a) {
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

        List<Territory> territoryList = new ArrayList<Territory>(possibles);

		Random rand = new Random();

        /**
         * 	for (int i = 0; i < territoryList.size(); i++) {
         Player self = PlayerUtils.getTerritoryOwner(currentState,
         territoryList.get(i));
         int strength = ArmyUtils.getArmiesOnTerritory(self,
         territoryList.get(i)).size();

         if (strength < 3) {
         this.currentStrongTerritories.add(territoryList.get(i));
         }

         }
         */


		switch (reason) {

		case PLACING_ARMIES_SET_UP:

			// Randomly selects a territory from the list of possible choices.
			int randomNumber = rand.nextInt(territoryList.size() - MIN )
					+ MIN;
		
			System.out.println("Undeployed armies: " + ArmyUtils.getUndeployedArmies(player).size());
            //REMEMBER TO CHANGE BACK
			return territoryList.get(randomNumber);

		case PLACING_REMAINING_ARMIES_PHASE:
			System.out.println();
			// Chooses a territory to add 1 of the remaining armies to.
			// If there are still armies reset the counter and start from the
			// beginning again.
			
			
			
			Territory currentTerritory = territoryList.get(this.initialDeploymentCounter);
			//TODO: fix bug - initialDeploymentCounter
			if (this.initialDeploymentCounter == territoryList.size() - 1) {
				this.initialDeploymentCounter = 0;
			} else {
				this.initialDeploymentCounter++;
			}

			return currentTerritory;

		case PLACING_ARMIES_PHASE:

			// Chooses a territory to add 1 of the remaining armies to.
			// If there are still armies reset the counter and start from the
			// beginning again.

            if (this.deploymentCounter < territoryList.size()) {
                return territoryList.get(0);
            }
			Territory currentTerritoryPlacing = territoryList.get(this.deploymentCounter);
			
			if (this.deploymentCounter == territoryList.size() - 1) {
				this.deploymentCounter = 0;
			} else {
				this.deploymentCounter++;
			}

			return currentTerritoryPlacing;

		case ATTACK_CHOICE_FROM:
            if (territoryList.size() == 0){
                return null;
            }
			return territoryList.get(attackFromCounter);

			
			
		case ATTACK_CHOICE_TO:

			// Creates an arraylist of the weakest enemy territories (1 soldier)
			// Chooses a random one of these and returns it.
			ArrayList<Territory> weakestTerritories = new ArrayList<Territory>();

			for (int i = 0; i < territoryList.size(); i++) {
				Player enemyOwner = PlayerUtils.getTerritoryOwner(currentState,
						territoryList.get(i));
				int numberOfEnemySoldiers = ArmyUtils
						.getNumberOfArmiesOnTerritory(enemyOwner,
								territoryList.get(i));

				if (numberOfEnemySoldiers < 10) {
					weakestTerritories.add(territoryList.get(i));
				}
			}

			int randomWeakest = rand.nextInt(weakestTerritories.size() - MIN
					+ 1)
					+ MIN;

            if(weakestTerritories.size() == 0){
                return null;
            }

			return weakestTerritories.get(0);

		case REINFORCEMENT_PHASE:
			return null; // TODO: Figure out average and reinforce depending on
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

	public void getCard(Player player, Card card) {
		// TODO Auto-generated method stub

	}

	public Card getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player,
			ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		
	    if(possibleCombinations.size() == 0)
	        	return null;

		Random rand = new Random();
		ArrayList<Triplet<Card, Card, Card>> possibles = CardUtils.getPossibleCardCombinations(currentState, player);
		return possibles.get(rand.nextInt(possibles.size()));
	}

    @Override
    public void reportStateChange(Change change) {

    }

	@Override
	public void createResponse() {
		// TODO Auto-generated method stub
		
	}

}
