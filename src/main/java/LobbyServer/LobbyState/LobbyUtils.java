package LobbyServer.LobbyState;

import GameState.Player;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.Response;
import LobbyServer.LobbyState.ObjectFromClient.JoinGameReq;
import org.java_websocket.WebSocket;

import java.util.Map.Entry;
import java.util.Set;

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
    
    public static void routeMessage(Lobby l, WebSocket socket, ClientMessage message) {
        PlayerConnection player = getPlayer(l, socket);
        player.setLatestResponse((Response) message);
    }

    public static void addGame(Lobby l, GameDescription g) {
        l.getunstartedGames().add(g);
    }

    public static void startGame(Lobby l, GameDescription g) {
        l.getunstartedGames().add(g);
    }

    public static boolean gameFull(Lobby l, int gameIndex) {
        return l.getunstartedGames().get(0).isReady();
    }

    public static void confirmJoined(WebSocket conn, int gameIndex) {
        JoinGameReq j = new JoinGameReq();
        j.gameIndex = gameIndex;
        conn.send(Jsonify.getObjectAsJsonString(j));
    }

    public static boolean addPlayerToGame(Lobby l, int gameIndex, WebSocket conn) {
        PlayerConnection player = getPlayer(l, conn);
        GameDescription g = l.getunstartedGames().get(gameIndex);
        if (!g.isReady()) {
            g.getPlayers().add(new Player(player));
            return true;
        }
        return false;
    }

    public static void startGames(Lobby l) {
        for (GameDescription g : l.getunstartedGames()) {
            if (g.isReady()) {
                g.startGame();
            }
        }
    }

    public static void addGameDescription(Lobby lobby, ClientMessage messageObject) {
    }

    public static void checkGameShouldStart(Lobby l, int gameIndex) {
        GameDescription g = l.getunstartedGames().get(gameIndex);
        if (g.isReady()) {
            g.startGame();
        }
    }

    public static void updateStateForLobbiedPlayers(Lobby lobby) {
        Set<Entry<WebSocket, PlayerConnection>> players = lobby.getPlayerConnections();
        for (Entry<WebSocket, PlayerConnection> p : players) {
            if (p.getKey().isOpen()) {
                p.getKey().send(Jsonify.getObjectAsJsonString(lobby));
            }
        }
    }
}
