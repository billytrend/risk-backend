package LobbyServer.LobbyState;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.*;
import PlayerInput.PlayerInterface;
import org.java_websocket.WebSocket;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class PlayerConnection implements PlayerInterface  {
    
    private Player playerState;
    private WebSocket connection;
	private CountDownLatch playerResponse = new CountDownLatch(0);
	private List<Response> responses = new ArrayList<Response>();

    public PlayerConnection(WebSocket connection) {
        this.playerState = new Player(this, 0, 100); // this last number to be changed
        this.connection = connection;
    }

	public void setLatestResponse(Response r) {
		responses.add(r);
		playerResponse.countDown();
	}
	
	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		
		DiceNumberRequest d = new DiceNumberRequest();
		d.max = max;
		d.reason = reason;

		connection.send(Jsonify.getObjectAsJsonString(d));
		
		playerResponse = new CountDownLatch(1);
		try {
			playerResponse.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ((DiceNumberResponse) responses.get(responses.size() - 1)).n;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles, boolean canResign, RequestReason reason) {
//        return possibles.iterator().next();
		TerritoryRequest t = new TerritoryRequest();
		t.possibles = possibles;
		t.reason = reason;
		t.canResign = canResign;

		connection.send(Jsonify.getObjectAsJsonString(t));

		playerResponse = new CountDownLatch(1);
		try {
			playerResponse.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        String chosenTerritoryName = ((TerritoryResponse) responses.get(responses.size() - 1)).territory;

        for (Territory terr : possibles) {
            if (chosenTerritoryName.equals(terr.getId())) return terr;
        }

		return null;

	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason) {
		ArmyRequest a = new ArmyRequest();
		a.max = max;
		a.reason = reason;

		connection.send(Jsonify.getObjectAsJsonString(a));

		playerResponse = new CountDownLatch(1);
		try {
			playerResponse.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return ((ArmyResponse) responses.get(responses.size() - 1)).n;
	}

	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		return null;
	}

    @Override
    public void reportStateChange(Change change) {
        connection.send(Jsonify.getObjectAsJsonString(change));
    }

}
