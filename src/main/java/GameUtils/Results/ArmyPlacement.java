package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;

public class ArmyPlacement extends ArmyChange {

	private String targetId;
		
	public ArmyPlacement(String playerId, String territoryId, int number, PlayState actionPlayed) {
		super(playerId, number, actionPlayed);
		targetId = territoryId;
	}
	
	public String getTargetTerritoryId(){
		return targetId;
	}
	

	@Override
	public String toString(){
		return super.toString() + "\n\tARMY PLACEMENT\n\t" + armyOwnerId + " places " + amount + " armies on " + targetId + ".";

	}
	
	@Override
	public void applyChange(State state) {
		Player armyOwner = state.lookUpPlayer(armyOwnerId);
		Territory target = state.lookUpTerritory(targetId);
		ArmyUtils.deployArmies(armyOwner, target, amount);
	}

}
