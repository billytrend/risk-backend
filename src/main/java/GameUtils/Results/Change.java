package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;

public abstract class Change {
	
	private Player actingPlayer;
	private PlayState actionPlayed;
    
    // this is for the ui, when it receives the object as json
    private final String changeType = getClass().getSimpleName();
    
	public Change(Player actingPlayer, PlayState actionPlayed){
		this.actingPlayer = actingPlayer;
		this.actionPlayed = actionPlayed;
	}
	
	
	public abstract void applyChange();
	
	public String toString(){
		return "---- PLAYER: " + actingPlayer.getId() + " ---- ACTION: " + actionPlayed.name();
	}
	
	public Player getActingPlayer(){
		return actingPlayer;
	}
	
	public PlayState getActionPlayed(){
		return actionPlayed;
	}
	

}
