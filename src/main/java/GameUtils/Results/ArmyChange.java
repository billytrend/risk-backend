package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;

public abstract class ArmyChange extends Change{
	protected Player armyOwner;
	protected int amount;
	
	public ArmyChange(Player player, int amount, PlayState action){
		super(player, action);
		armyOwner = player;
		this.amount = amount;
	}
	
	public Player gerArmyOwner(){
		return armyOwner;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public abstract void applyChange();

	
}
