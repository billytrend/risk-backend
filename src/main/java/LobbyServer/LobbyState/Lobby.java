package LobbyServer.LobbyState;

import GameState.State;

import java.util.HashSet;

public class Lobby {
    
    private HashSet<PlayerConnection> playerConnections = new HashSet<PlayerConnection>();
    private HashSet<State> currentGames = new HashSet<State>();

    public HashSet<PlayerConnection> getPlayerConnections() {
        return playerConnections;
    }

    public HashSet<State> getCurrentGames() {
        return currentGames;
    }
}
