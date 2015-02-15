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
	
	public String toString(){
		if(movingFrom != null)
			return "owner: " + armyOwner.getId() + " from: " + movingFrom.getId() + 
					"  to: " + movingTo.getId() + " number: " + amount;
		else
			return "owner: " + armyOwner.getId() + 
					"  to: " + movingTo.getId() + " number: " + amount;
	}
}
