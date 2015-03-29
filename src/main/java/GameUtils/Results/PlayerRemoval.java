package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;

public class PlayerRemoval extends Change{
	
	private String removedPlayerId;
	
	public PlayerRemoval(String actingPlayerId, String removedPlayerId) {
		super(actingPlayerId, PlayState.PLAYER_INVADING_COUNTRY);
		this.removedPlayerId = removedPlayerId;
	}
	
	public String getRemovedPlayerId(){
		return removedPlayerId;
	}

	public void applyChange(State state) {
		Player removedPlayer = state.lookUpPlayer(removedPlayerId);
		PlayerUtils.removePlayer(state, removedPlayer);
	}
	
	public String toString(){
		return super.toString() + "\n\tPLAYER REMOVAL\n\t" + removedPlayerId;
	}

}
