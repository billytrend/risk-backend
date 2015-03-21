package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;

public abstract class ArmyChange extends Change{
	protected String armyOwnerId;
	protected int amount;
	
	public ArmyChange(String playerId, int amount, PlayState action){
		super(playerId, action);
		armyOwnerId = playerId;
		this.amount = amount;
	}
	
	public String gerArmyOwner(){
		return armyOwnerId;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public abstract void applyChange(State state);

}
