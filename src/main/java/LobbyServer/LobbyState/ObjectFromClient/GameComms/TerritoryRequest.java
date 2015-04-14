package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameEngine.RequestReason;
import GameState.Territory;

import java.util.HashSet;

public class TerritoryRequest extends Request {
    public final String requestType = "territory";
    public HashSet<Territory> possibles;
	public boolean canResign;

    public TerritoryRequest(RequestReason reason) {
        super(reason);
        switch (reason) {
            case REINFORCEMENT_PHASE : humanRequest += "Choose a territory to redeploy from"; break;
            case PLACING_ARMIES_SET_UP: humanRequest += "Claim a territory"; break;
            case PLACING_REMAINING_ARMIES_PHASE:
            case PLACING_ARMIES_PHASE: humanRequest += "Choose a territory to reinforce"; break;
            case ATTACK_CHOICE_FROM: humanRequest += "Choose a territory to attack from"; break;
            case ATTACK_CHOICE_TO: humanRequest += "Choose a territory to attack to"; break;
            default: break;
        }
        humanRequest += ".";
    }
}
