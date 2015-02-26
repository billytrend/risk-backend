package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;

public class PlayerRemoval extends Change{
	
	private Player removedPlayer;
	private State state;
	
	public PlayerRemoval(Player actingPlayer, Player removedPlayer, State state) {
		super(actingPlayer, PlayState.PLAYER_INVADING_COUNTRY);
		this.removedPlayer = removedPlayer;
		this.state = state;
	}
	
	public Player getRemovedPlayer(){
		return removedPlayer;
	}

	@Override
	public void applyChange() {
		PlayerUtils.removePlayer(state, removedPlayer);
	}
	
	public String toString(){
		return super.toString() + "\n\tPLAYER REMOVAL\n\t" + removedPlayer.getId();
	}

}
