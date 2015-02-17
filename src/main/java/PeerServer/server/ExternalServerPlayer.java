package PeerServer.server;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerInterface;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class ExternalServerPlayer implements PlayerInterface {

    private CountDownLatch waitingOnExternalResponse = new CountDownLatch(0);
    private Object recentResponse;


    public void setPlayerResponse(Object responseObject) {
        recentResponse = responseObject;
        waitingOnExternalResponse.countDown();
    }
    
    @Override
    public int getNumberOfDice(Player player, int max, RequestReason reason) {
        this.waitingOnExternalResponse = new CountDownLatch(1);
        try {
            this.waitingOnExternalResponse.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Territory getTerritory(Player player, HashSet<Territory> possibles, boolean canResign, RequestReason reason) {
        return null;
    }

    @Override
    public int getNumberOfArmies(Player player, int max, RequestReason reason) {
        return 0;
    }

    @Override
    public void giveCard(Player player, Card card) {

    }

    @Override
    public Card getCardOptions() {
        return null;
    }
}
