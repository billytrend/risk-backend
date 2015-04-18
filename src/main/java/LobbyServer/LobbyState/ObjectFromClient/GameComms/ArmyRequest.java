package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameEngine.RequestReason;
import GameState.Territory;

public class ArmyRequest extends Request {
    public final String requestType = "number_of_armies";
	public int max;

    public ArmyRequest(RequestReason reason, Territory to, Territory from) {
        super(reason);
        switch (reason) {
            case PLACING_ARMIES_PHASE : humanRequest = "Please choose how many armies to place on " + to.getId(); break;
            case REINFORCEMENT_PHASE : humanRequest = "Please choose how many armies to move from " + from.getId() + " to " + to.getId(); break;
            case POST_ATTACK_MOVEMENT : humanRequest = "Please choose how many more armies you'd like to move from " + from.getId() + " to " + to.getId(); break;
            default: break;
        }
        humanRequest += ".";
    }
}
