package PlayerInput;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import GameEngine.PlayState;
import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.PlayerUtils;

public class CommunistDefensive implements PlayerInterface {
	
	public State currentState;
	private int initialDeploymentCounter;
	private int deploymentCounter;
	private static final int MIN = 0;
	
	public CommunistDefensive(State a)
	{
		this.initialDeploymentCounter = 0;
		this.deploymentCounter = 0;
		this.currentState = a;
	}



	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		return max;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {

		
		//NEEDS TO BE FINISHED
		Territory[] territoryArray = (Territory[]) possibles.toArray();
		Random rand = new Random();
		
		switch (reason) {
		case PLACING_ARMIES_SET_UP:
			int randomNumber = rand.nextInt(territoryArray.length - MIN + 1) + MIN;
			return territoryArray[randomNumber];
			
		case PLACING_REMAINING_ARMIES_PHASE:
			
			Territory currentTerritory = territoryArray[this.initialDeploymentCounter];
			
			if(this.initialDeploymentCounter == territoryArray.length - 1){
				this.initialDeploymentCounter = 0;
			} else {
				this.initialDeploymentCounter++;
			}
			
			return currentTerritory;
			
		case PLACING_ARMIES_PHASE:
			return null;
			
			
		case ATTACK_CHOICE:
			return null;
			
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
		case ATTACK_CHOICE:
			return max;
		case DEFEND_CHOICE:
			return max;
		case REINFORCEMENT_PHASE:
			return 0; // TODO: Figure out average and reinforce depending on
						// links.
		case POST_ATTACK_MOVEMENT:
			return max - 2;
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
