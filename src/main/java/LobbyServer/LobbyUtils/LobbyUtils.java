package LobbyServer.LobbyUtils;

import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.PlayerConnection;
import org.java_websocket.WebSocket;

public class LobbyUtils {
    public static PlayerConnection getPlayer(Lobby l, WebSocket socket) {
        return l.getPlayerConnectionsMap().get(socket);
    }

}
