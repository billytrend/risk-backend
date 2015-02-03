package LobbyServer.LobbyState;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerInterface;

import org.java_websocket.WebSocket;

import java.util.HashSet;

public class PlayerConnection implements PlayerInterface  {
    
    private Player playerState;
    private WebSocket connection;

    public PlayerConnection(WebSocket connection) {
        this.playerState = new Player(this, 0, 100); // this last number to be changed
        this.connection = connection;
    }

	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void giveCard(Player player, Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Card getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
