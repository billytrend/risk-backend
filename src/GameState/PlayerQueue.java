package GameState;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerQueue implements Iterator {

    private int firstPlayer = 0;
    private int curPlayer = -1;
    private final ArrayList<Player> players;

    public PlayerQueue(ArrayList<Player> players) {
        this.players = players;
    }

    public void setFirstPlayer(int firstPlayer) {

        if (firstPlayer < this.players.size()) {
            this.firstPlayer = firstPlayer;
        }

    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Player next() {
        if (this.curPlayer == -1) {
            this.curPlayer = this.firstPlayer;
        }

        else {
            this.curPlayer++;
            this.curPlayer = this.curPlayer % this.players.size();
        }

        return this.players.get(curPlayer);
    }

    public Player getCurrent() {
        return this.players.get(this.curPlayer);
    }

}
