package GameUtils;

import GameState.*;

import java.util.ArrayList;
import java.util.HashSet;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * Holds all the methods that deal with any kind of
 * 'player management' such as providing information about
 * players.
 *
 */
public class PlayerUtils {


    /**
     * @param p
     * @return
     */
    public static int getNumberOfTerritoriesOwned(Player p) {
        return TerritoryUtils.getPlayersTerritories(p).size();
    }

    /**
     *
     * @param state
     * @param t
     * @return
     * */
    public static Player getTerritoryOwner(State state, Territory t) {
        for (Player p : state.getPlayers()) {
            if (TerritoryUtils.getPlayersTerritories(p).contains(t)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Counts players that are still in the game 
     * 
     * @param state
     * @return
     */
     public static int countPlayers(State state) {
        return state.getPlayerQueue().getNumberOfCurrentPlayers();
     }
     
     /**
      * 
      * @param state
      * @return
      */
     public static ArrayList<Player> getPlayersInGame(State state){
    	 return state.getPlayerQueue().getCurrentPlayers();
     }

    /**
     * @param p
     * @return
     */
    public static boolean playerIsOut(Player p) {
    	debug("\t\tNumber owned: " + getNumberOfTerritoriesOwned(p));
        return getNumberOfTerritoriesOwned(p) < 1;
    }

    /**
     * 
     * @param gameState
     * @param player
     */
	public static void removePlayer(State gameState, Player player) {
		gameState.getPlayerQueue().removePlayer(player);
	}
    
}
