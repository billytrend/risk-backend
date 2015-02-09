package PlayerInput;

import java.util.HashSet;

import GameEngine.PlayState;
import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.PlayerUtils;

public class CommunistDefensive implements PlayerInterface {

	private int averageToDeploy;
	
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
		
		
		switch(reason){
	    case PLACING_ARMIES_SET_UP :
	       return 1;
	    case PLACING_REMAINING_ARMIES_PHASE :
	       return 1;
	    //You can have any number of case statements.
	    default : //Optional
	       //Statements
	}
		
		
		
		if (reason.equals(PlayState.BEGINNING_STATE)){
			return 1;
		}
		
		if (reason.equals(PlayState.FILLING_EMPTY_COUNTRIES)){
			return 1;
		}
		
		if (reason.equals(PlayState.PLAYER_PLACING_ARMIES)){
			return 1;
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
