package PlayerInput.PlayerChoice;

import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;

public class Choice extends ClientMessage {

    private boolean endGo = false;

    public void setEndGo() {
        this.endGo = true;
    }

    public boolean isEndGo() {
        return endGo;
    }
}
