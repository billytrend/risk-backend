package GameState;

import java.util.Iterator;

public class PlayerQueue implements Iterator {

    private int firstPlayer = 0;
    private int curPlayer = -1;

    public void setFirstPlayer(int firstPlayer) {

        if (firstPlayer < State.countPlayers()) {
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
            this.curPlayer = this.curPlayer % State.countPlayers();
        }

        return State.getPlayer(this.curPlayer);
    }

    public Player getCurrent() {
        return State.getPlayer(this.curPlayer);
    }

}
