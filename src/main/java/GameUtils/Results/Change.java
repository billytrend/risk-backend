package GameUtils.Results;

import java.util.ArrayList;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;

public abstract class Change {
	
	private String actingPlayerId;
	private PlayState actionPlayed;

	public Change(String actingPlayerId, PlayState actionPlayed){
		this.actingPlayerId = actingPlayerId;
		this.actionPlayed = actionPlayed;
	}
	
	
	public abstract void applyChange(State state);
	
	public String toString(){
		return "---- PLAYER: " + actingPlayerId + " ---- ACTION: " + actionPlayed.name();
	}
	
	public String getActingPlayerId(){
		return actingPlayerId;
	}
	
	public PlayState getActionPlayed(){
		return actionPlayed;
	}
	

}
