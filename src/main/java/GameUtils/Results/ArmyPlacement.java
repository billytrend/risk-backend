package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.Territory;
import GameUtils.ArmyUtils;

public class ArmyPlacement extends ArmyChange {

	private Territory target;
		
	public ArmyPlacement(Player player, Territory territory, int number, PlayState actionPlayed) {
		super(player, number, actionPlayed);
		target = territory;
	}
	
	public Territory getTargetTerritory(){
		return target;
	}
	

	@Override
	public String toString(){
		return super.toString() + "\n\tARMY PLACEMENT\n\t" + armyOwner.getId() + " places " + amount + " armies on " + target.getId() + ".";

	}
	
	@Override
	public void applyChange() {
		ArmyUtils.deployArmies(armyOwner, target, amount);
	}

}
