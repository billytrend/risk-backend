package LobbyServer.LobbyState;

import GameState.State;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lobby {
    
    private HashMap<WebSocket, PlayerConnection> playerConnections = new HashMap<WebSocket, PlayerConnection>();
    private HashSet<State> currentGames = new HashSet<State>();

    public Set<Map.Entry<WebSocket, PlayerConnection>> getPlayerConnections() {
        return new HashSet<Map.Entry<WebSocket, PlayerConnection>>(playerConnections.entrySet());
    }

    public HashSet<State> getCurrentGames() {
        return currentGames;
    }
    
    public HashMap<WebSocket, PlayerConnection> getPlayerConnectionsMap() {
        return this.playerConnections;
    }
}
