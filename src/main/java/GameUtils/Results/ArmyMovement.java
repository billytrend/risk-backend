package GameUtils.Results;

import GameState.Player;
import GameState.Territory;

public class ArmyMovement {
	private Player armyOwner;
	private Territory movingFrom;
	private Territory movingTo;
	private int amount;
	
	public ArmyMovement(Player player, Territory from, Territory to, int number){
		armyOwner = player;
		movingFrom = from;
		movingTo = to;
		amount = number;
	}
	
	public Player getArmyOwner(){
		return armyOwner;
	}
	
	public Territory getMovingFromTerritory(){
		return movingFrom;
	}
	
	public Territory getMovingToTerritory(){
		return movingTo;
	}
	
	public int getAmount(){
		return amount;
	}
}
