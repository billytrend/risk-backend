package GameState;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The class enabling to iterate through all players
 * that are still in the game - that haven't lost.
 *
 */
public class PlayerQueue implements Iterator {

    private int firstPlayer = 0;
    private int curPlayer = 0;
    private ArrayList<Player> players;

    public PlayerQueue(ArrayList<Player> players1) {
    	players = new ArrayList<Player>();
    	players.addAll(players1);
    }

    public void setFirstPlayer(int firstPlayer) {
        if (firstPlayer < this.players.size()) {
            this.firstPlayer = firstPlayer;
        }
        curPlayer = firstPlayer;
    }
    
    public int getFirstPlayer() {
        return firstPlayer;
    }
    
    
    public int getNumberOfCurrentPlayers(){
    	return players.size();
    }

    public ArrayList<Player> getCurrentPlayers(){
    	return players;
    }
    
    public Player getCurrent() {
        return this.players.get(this.curPlayer);
    }
    
    /**
     * Called when a player has lost all their territories
     * and have to be removed from the game.
     * 
     * TODO: It should be also used when a player dissapears
     * from the game (connection is lost etc.)
     * 
     * @param p
     */
    public void removePlayer(Player p){
    	players.remove(p);
    }
    
    @Override
    public void remove() {
    }
    
    @Override
    public boolean hasNext() {
        if(players.size() == 1)
        	return false;
        else
        	return true;
    }

    @Override
    public Player next() {
        if (curPlayer == -1) {
            curPlayer = firstPlayer;
        }

        else {
            curPlayer++;
            curPlayer = curPlayer % players.size();
        }

        return players.get(curPlayer);
    }




}
