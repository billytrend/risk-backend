package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameEngine.RequestReason;
import GameState.Card;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class CardRequest extends Request {
    public final String requestType = "card";
    public ArrayList<Triplet<Card, Card, Card>> possibles;

    public CardRequest(RequestReason reason) {
        super(reason);
        humanRequest += ".";
    }
}
