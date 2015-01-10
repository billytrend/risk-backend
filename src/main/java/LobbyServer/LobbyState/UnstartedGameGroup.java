package LobbyServer.LobbyState;

import java.util.HashSet;

public class UnstartedGameGroup {
    
    private final String id;
    private final int maxPlayers;
    private HashSet<PlayerConnection> players = new HashSet<PlayerConnection>();
    
    public UnstartedGameGroup(String id, int maxPlayers) {
        this.id = id;
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getId() {
        return id;
    }

    public HashSet<PlayerConnection> getPlayers() {
        return players;
    }
}
