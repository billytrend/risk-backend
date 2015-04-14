package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameEngine.RequestReason;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;

public class Request extends ClientMessage {
	public RequestReason reason;
    public String humanRequest = "";

    public Request(RequestReason reason) {
        this.reason = reason;
    }
}
