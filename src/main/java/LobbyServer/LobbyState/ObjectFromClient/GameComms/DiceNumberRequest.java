package LobbyServer.LobbyState.ObjectFromClient.GameComms;

public class DiceNumberRequest extends Request {
    public final String requestType = "number_of_dice";
    public int max;
}
