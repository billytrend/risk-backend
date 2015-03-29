package GameUtils.Results;

import GameEngine.PlayState;
import GameState.State;

public abstract class Change {
	
	private String actingPlayerId;
	private PlayState actionPlayed;

    // this is for the ui, when it receives the object as json
    private final String changeType = getClass().getSimpleName();

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
