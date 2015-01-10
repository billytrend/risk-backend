package LobbyServer.LobbyUtils;

import LobbyServer.LobbyState.Lobby;
import LobbyServer.LobbyState.PlayerConnection;
import LobbyServer.LobbyState.UnstartedGameGroup;
import org.java_websocket.WebSocket;

import java.util.HashSet;

public class UnstartedGameUtils {
    
    public void addPlayerToUnstartedGame(UnstartedGameGroup g, PlayerConnection p) {
        if (g.getPlayers().size() < g.getMaxPlayers()) {
            g.getPlayers().add(p);
        }
    }

    public void removePlayerFromUnstartedGame(UnstartedGameGroup g, PlayerConnection p) {
        g.getPlayers().remove(p);
    }

    public HashSet<PlayerConnection> getPlayersInGroup(HashSet<UnstartedGameGroup> groups) {
        HashSet<PlayerConnection> groupedPlayers = new HashSet<PlayerConnection>();
        for (UnstartedGameGroup set : groups) {
            groupedPlayers.addAll(set.getPlayers());
        }
        return groupedPlayers;
    }

    public HashSet<PlayerConnection> getPlayersNotInGroup(HashSet<UnstartedGameGroup> groups, HashSet<PlayerConnection> a) {
        HashSet<PlayerConnection> groupedPlayers = getPlayersInGroup(groups);
        HashSet<PlayerConnection> allPlayers = new HashSet<PlayerConnection>(a);
        allPlayers.remove(groupedPlayers);
        return allPlayers;
    }
    

}
