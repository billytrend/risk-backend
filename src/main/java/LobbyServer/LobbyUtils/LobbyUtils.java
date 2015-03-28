package LobbyServer.LobbyUtils;

import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.Response;
import LobbyServer.LobbyState.PlayerConnection;
import org.java_websocket.WebSocket;

import java.util.Map.Entry;

public class LobbyUtils {
    
    public static PlayerConnection getPlayer(Lobby l, WebSocket socket) {
        return l.getPlayerConnectionsMap().get(socket);
    }
    
    public static void addConnection(Lobby l, WebSocket socket, PlayerConnection player) {
        l.getPlayerConnectionsMap().put(socket, player);
    }

    public static void sendToEveryOne(Lobby l, Object o) {
        String jsonO = Jsonify.getObjectAsJsonString(o);
        for (Entry<WebSocket, PlayerConnection> p : l.getPlayerConnections()) {
            p.getKey().send(jsonO);
        }
    }
    
    public static void routeMessage(Lobby l, WebSocket socket, ClientMessage message) {
        PlayerConnection player = getPlayer(l, socket);
        player.setLatestResponse((Response) message);
    }
    
}
