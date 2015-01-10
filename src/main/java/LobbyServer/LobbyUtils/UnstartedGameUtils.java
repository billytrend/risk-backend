package LobbyServer.LobbyUtils;

import LobbyServer.LobbyState.PlayerConnection;
import LobbyServer.LobbyState.UnstartedGameGroup;

public class UnstartedGameUtils {
    
    public void addPlayerToUnstartedGame(UnstartedGameGroup g, PlayerConnection p) {
        if (g.getPlayers().size() < g.getMaxPlayers()) {
            g.getPlayers().add(p);
        }
    }
}
