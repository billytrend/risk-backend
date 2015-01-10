package LobbyServer.LobbyState;

import GameState.Army;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerChoice.Choice;
import PlayerInput.PlayerInterface;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class PlayerConnection implements PlayerInterface {
    
    private Player playerState;
    private WebSocket connection;
    private CountDownLatch waiter = null;
    private Choice mostRecentChoice = null;

    public PlayerConnection(String id, WebSocket connection) {
        this.playerState = new Player(id, this, 0);
        this.connection = connection;
    }

    private PlayerConnection getWaiter() {
        this.waiter = new CountDownLatch(1);
        return this;
    }
    
    @Override
    public Choice getChoice() {
        return this.mostRecentChoice;
    }

    @Override
    public Choice await() {
        try {
            waiter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return this.getChoice();
    }

    @Override
    public void setChoice(Choice ch) {
        this.mostRecentChoice = ch;
        waiter.countDown();
    }

    @Override
    public PlayerConnection getNumberOfDice(Player player, int max) {
        
        return getWaiter();
    }

    @Override
    public PlayerConnection getTerritory(Player player, HashSet<Territory> possibles) {

        return getWaiter();
    }

    @Override
    public PlayerConnection getNumberOfArmies(Player player, ArrayList<Army> playersUndeployedArmies) {
        return getWaiter();
    }

    @Override
    public PlayerConnection giveCard(Player player, Card card) {
        return getWaiter();
    }

    @Override
    public PlayerConnection getCardOptions() {
        return getWaiter();
    }
}
