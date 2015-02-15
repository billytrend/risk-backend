package GameUtils.Results;

import GameState.Player;
import GameState.Territory;

public class ArmyDeletion {
	private Player armyOwner;
	private int amount;
	private Territory territory;
	
	public ArmyDeletion(Player player, Territory territory, int number){
		armyOwner = player;
		amount = number;
		this.territory = territory;
	}
	
	public Player gerArmyOwner(){
		return armyOwner;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public Territory getTerritory(){
		return territory;
	}
}
