package LobbyServer.LobbyState;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GameEngine.GameEngine;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;

import java.util.ArrayList;

public class GameDescription extends ClientMessage {
    
    private final String id;
    private final int maxPlayers;
    private ArrayList<Player> players = new ArrayList<Player>();
    private State gameState;
    private transient Thread thread;
    
    public GameDescription(String id, int maxPlayers) {
        this.id = id;
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    private void makeGameState() {
        gameState = new State();
        RiskMapGameBuilder.addRiskTerritoriesToState(gameState);
        RiskMapGameBuilder.colourPlayers(players);
        gameState.setPlayers(players);
    }

    public Thread startGame() {
        this.makeGameState();
        thread = new Thread(new GameEngine(gameState));
        thread.start();
        return thread;
    }

    public boolean isReady() {
        if (players.size() == maxPlayers) {
            return true;
        }
        return false;
    }
}
