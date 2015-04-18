package LobbyServer.LobbyState;

import org.java_websocket.WebSocket;

import java.util.*;

public class Lobby {
    
    private HashMap<WebSocket, PlayerConnection> playerConnections = new HashMap<WebSocket, PlayerConnection>();
    private LinkedList<GameDescription> unstartedGames = new LinkedList<GameDescription>();
    private LinkedList<GameDescription> startedGames = new LinkedList<GameDescription>();

    public Set<Map.Entry<WebSocket, PlayerConnection>> getPlayerConnections() {
        return new HashSet<Map.Entry<WebSocket, PlayerConnection>>(playerConnections.entrySet());
    }

    public LinkedList<GameDescription> getunstartedGames() {
        return unstartedGames;
    }

    public LinkedList<GameDescription> getStartedGames() {
        return startedGames;
    }

    public HashMap<WebSocket, PlayerConnection> getPlayerConnectionsMap() {
        return this.playerConnections;
    }



}
