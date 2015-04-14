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
        this.playerState = new Player(this, 100); // this last number to be changed
        this.connection = connection;
    }

	public void setLatestResponse(Response r) {
		responses.add(r);
		playerResponse.countDown();
	}
	
	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
//		return max;
		DiceNumberRequest d = new DiceNumberRequest(reason);
		d.max = max;

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
		TerritoryRequest t = new TerritoryRequest(reason);
		t.possibles = possibles;
		t.canResign = canResign;

		connection.send(Jsonify.getObjectAsJsonString(t));

		playerResponse = new CountDownLatch(1);
		try {
			playerResponse.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        String chosenTerritoryName = ((TerritoryResponse) responses.get(responses.size() - 1)).territory;

        if (chosenTerritoryName == null) return null;

        for (Territory terr : possibles) {
            if (chosenTerritoryName.equals(terr.getId())) return terr;
        }

		return null;

	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
		ArmyRequest a = new ArmyRequest(reason, to, from);
		a.max = max;

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
        CardRequest c = new CardRequest(null);
        c.possibles = possibleCombinations;

        connection.send(Jsonify.getObjectAsJsonString(c));

        playerResponse = new CountDownLatch(1);
        try {
            playerResponse.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ( possibleCombinations.get(((CardResponse) responses.get(responses.size() - 1)).index));
	}

    @Override
    public void reportStateChange(Change change) {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connection.send(Jsonify.getObjectAsJsonString(change));
    }

}
