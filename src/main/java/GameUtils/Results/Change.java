package GameUtils.Results;

import java.util.ArrayList;

import GameEngine.PlayState;
import GameState.Player;

public abstract class Change {
	
	private Player actingPlayer;
	private PlayState actionPlayed;

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
