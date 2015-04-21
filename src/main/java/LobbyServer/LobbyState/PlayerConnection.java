package LobbyServer.LobbyState;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.*;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import org.java_websocket.WebSocket;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class PlayerConnection implements PlayerInterface  {

    public boolean dead = false;
    private Player playerState;
    private WebSocket connection;
	private CountDownLatch playerResponse = new CountDownLatch(0);
	private List<Response> responses = new ArrayList<Response>();
    private boolean activations[] = new boolean[] { true, true, true, true };
    private DumbBotInterface backup = new DumbBotInterface();

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
		if (!activations[0]) return max;

		DiceNumberRequest d = new DiceNumberRequest(reason);
		d.max = max;
		d.defending = defending;
		d.attacking = attacking;

		try {
            connection.send(Jsonify.getObjectAsJsonString(d));
            playerResponse = new CountDownLatch(1);
            playerResponse.await();
		} catch (Exception e) {
            return backup.getNumberOfArmies(player, max, reason, attacking, defending);
		}

		return ((DiceNumberResponse) responses.get(responses.size() - 1)).n;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,Territory from, boolean canResign, RequestReason reason) {
        if (!activations[1]) return possibles.iterator().next();

        TerritoryRequest t = new TerritoryRequest(reason);
		t.possibles = possibles;
		t.canResign = canResign;

		try {
            connection.send(Jsonify.getObjectAsJsonString(t));
            playerResponse = new CountDownLatch(1);
            playerResponse.await();
		} catch (Exception e) {
            return backup.getTerritory(player, possibles, from, canResign, reason);
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
        if (!activations[2]) return max;
        ArmyRequest a = new ArmyRequest(reason, to, from);
		a.max = max;

        try {
            connection.send(Jsonify.getObjectAsJsonString(a));
            playerResponse = new CountDownLatch(1);
            playerResponse.await();
        } catch (Exception e) {
            return backup.getNumberOfArmies(player, max, reason, to, from);
        }

		return ((ArmyResponse) responses.get(responses.size() - 1)).n;
	}

	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        if (!activations[3]) return possibleCombinations.get(0);
        CardRequest c = new CardRequest(null);
        c.possibles = possibleCombinations;

        try {
            connection.send(Jsonify.getObjectAsJsonString(c));
            playerResponse = new CountDownLatch(1);
            playerResponse.await();
        } catch (Exception e) {
            return backup.getCardChoice(player, possibleCombinations);
        }

        if (((CardResponse) responses.get(responses.size() - 1)).index < 0) return null;

        return ( possibleCombinations.get(((CardResponse) responses.get(responses.size() - 1)).index));
	}

    @Override
    public void reportStateChange(Change change) {
        try {
            connection.send(Jsonify.getObjectAsJsonString(change));
            Thread.sleep(20);
        } catch (Exception e) {
            dead = true;
        }
    }

    @Override
    public void createResponse() {

    }


}
