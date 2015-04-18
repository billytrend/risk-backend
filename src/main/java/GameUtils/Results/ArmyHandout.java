package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameUtils.ArmyUtils;

public class ArmyHandout extends ArmyChange {

	public ArmyHandout(String playerId, int number, PlayState playState) {
		super(playerId, number, playState);
	}

	@Override
	public String toString(){
		return super.toString() + "\n\tARMY HANDOUT\n\t" + armyOwnerId + " gets " + amount + ".";
	}
	
	@Override
	public void applyChange(State state) {
		Player armyOwner = state.lookUpPlayer(armyOwnerId);
		ArmyUtils.givePlayerNArmies(armyOwner, amount);
	}

}
