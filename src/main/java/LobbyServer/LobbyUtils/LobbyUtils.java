package LobbyServer.LobbyUtils;

import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.PlayerConnection;
import org.java_websocket.WebSocket;

import java.util.Map.Entry;

public class LobbyUtils {
    
    public static PlayerConnection getPlayer(Lobby l, WebSocket socket) {
        return l.getPlayerConnectionsMap().get(socket);
    }
    
    public static void addConnection(Lobby l, WebSocket socket) {
        l.getPlayerConnectionsMap().put(socket, new PlayerConnection(socket));
    }

    public static void sendToEveryOne(Lobby l, Object o) {
        String jsonO = Jsonify.getObjectAsJsonString(o);
        for (Entry<WebSocket, PlayerConnection> p : l.getPlayerConnections()) {
            p.getKey().send(jsonO);
        }
    }
    
    
}
