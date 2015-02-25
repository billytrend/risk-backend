package GameUtils.Results;

import GameEngine.GameEngine;
import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;

public class ArmyMovement extends ArmyChange{
	
	private Territory source;
	private Territory target;
	
	public ArmyMovement(Player player, Territory source, Territory target, int number, PlayState actionPlayed) {
		super(player, number, actionPlayed);
		this.source = source;
		this.target = target;
	}
	

	public Territory getTargetTerritory(){
		return target;
	}
	
	public Territory getSourceTerritory(){
		return source;
	}

	public String toString(){
		return super.toString() + "\n\tARMY MOVEMENT\n\t" + armyOwner.getId() + " moves " + 
				amount + " armies from " + source.getId() + " to " + target.getId() + ".";
	}
	
	public void applyChange(){
			ArmyUtils.moveArmies(armyOwner, source, target, amount);
	}
}
