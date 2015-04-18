package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameEngine.RequestReason;
import GameState.Territory;

public class DiceNumberRequest extends Request {
    public final String requestType = "number_of_dice";
    public Territory attacking;
    public Territory defending;
    public int max;
            

    public DiceNumberRequest(RequestReason reason) {
        super(reason);
        switch (reason) {
            case ATTACK_CHOICE_DICE : humanRequest += "Please choose how many dice to attack with"; break;
            case DEFEND_CHOICE_DICE : humanRequest += "Please choose how many dice to defend with"; break;
            default: break;
        }
        humanRequest += ".";
    }
}
