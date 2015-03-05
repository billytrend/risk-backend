package GameUtils.Results;

import GameEngine.GameEngine;
import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;

public class ArmyMovement extends ArmyChange{
	
	private String sourceId;
	private String targetId;
	
	public ArmyMovement(String playerId, String sourceId, String targetId, int number, PlayState actionPlayed) {
		super(playerId, number, actionPlayed);
		this.sourceId = sourceId;
		this.targetId = targetId;
	}
	

	public String getTargetTerritoryId(){
		return targetId;
	}
	
	public String getSourceTerritoryId(){
		return sourceId;
	}

	public String toString(){
		return super.toString() + "\n\tARMY MOVEMENT\n\t" + armyOwnerId + " moves " + 
				amount + " armies from " + sourceId + " to " + targetId + ".";
	}
	
	public void applyChange(State state){
		Player armyOwner = state.lookUpPlayer(armyOwnerId);
		Territory target = state.lookUpTerritory(targetId);
		Territory source = state.lookUpTerritory(sourceId);
		ArmyUtils.moveArmies(armyOwner, source, target, amount);
	}
}
