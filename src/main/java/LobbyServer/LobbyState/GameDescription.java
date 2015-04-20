package LobbyServer.LobbyState;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import PlayerInput.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameDescription extends ClientMessage {
    
    private final String id;
    private final int maxPlayers;
    private final ArrayList<String> AIs = new ArrayList<String>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private transient ArrayList<PlayerInterface> ghosts = new ArrayList<PlayerInterface>();
    private State gameState;
    private transient Thread thread;
    
    public GameDescription(String id, int maxPlayers) {
        this.id = id;
        this.maxPlayers = maxPlayers;
    }

    public ArrayList<PlayerInterface> getGhosts() {
        return ghosts;
    }

    public ArrayList<String> getAIs() {
        return AIs;
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
        ArrayList<PlayerInterface> ais = getAIs(gameState);
        for (PlayerInterface p : ais) {
            players.add(new Player(p));
        }
        RiskMapGameBuilder.addRiskTerritoriesToState(gameState);
        RiskMapGameBuilder.colourPlayers(players);
        gameState.setPlayers(players);
        if (ghosts != null) {
            for (PlayerInterface p : ghosts) {
                gameState.addGhost(p);
            }
        }
    }

    public Thread startGame() {
        this.makeGameState();
        thread = new Thread(new GameEngine(gameState));
        thread.start();
        return thread;
    }

    public ArrayList<PlayerInterface> getAIs(State state) {
        HashMap<String, Class<? extends PlayerInterface>> ais = new HashMap<String, Class<? extends PlayerInterface>>();
        ais.put(Berserker.class.getSimpleName(), Berserker.class);
        ais.put(BorderControl.class.getSimpleName(), BorderControl.class);
        ais.put(CommunistAggressive.class.getSimpleName(), CommunistAggressive.class);
        ais.put(CommunistDefensive.class.getSimpleName(), CommunistDefensive.class);
        ais.put(DumbBotInterface.class.getSimpleName(), DumbBotInterface.class);

        ArrayList<PlayerInterface> aiObjects = new ArrayList<PlayerInterface>();
        for (String ai : this.AIs) {
            try {
                Constructor<?> constructor = ais.get(ai).getConstructor(State.class);
                PlayerInterface object = (PlayerInterface) constructor.newInstance(new Object[] { gameState });
                aiObjects.add(object);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return aiObjects;
    }

    public boolean isReady() {
        if (players.size() == maxPlayers) {
            return true;
        }
        return false;
    }

    public void addGhost(PlayerConnection playerConnection) {
        if (ghosts == null) ghosts = new ArrayList<PlayerInterface>();
        ghosts.add(playerConnection);
    }
}
