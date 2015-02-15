package GameUtils.Results;

import java.util.ArrayList;
import java.util.Iterator;

import GameState.Player;


public class StateChangeRecord {
	
	private static ArrayList<StateChange> gameStateChanges = new ArrayList<StateChange>();
	private static StateChange lastChange;
	
	public static void clearRecord(){
		gameStateChanges = new ArrayList<StateChange>();
	}
	
	/**
	 * Adds a given state change to the record of all changes
	 * 
	 * @param change
	 */
	public static void addStateChange(StateChange change){
		gameStateChanges.add(change);
		lastChange = change;
	}
	
	/**
	 * Returns the last change that was added
	 * @return
	 */
	public static StateChange getLastChange(){
		return lastChange;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getSize(){
		return gameStateChanges.size();
	}
	
	/**
	 * Returns an ArrayList of n most recent state changes,
	 * with the first index being the least recent and the
	 * last index being last (most recent) state change
	 * If the user requests more changes than the number of
	 * recorded changes the method returns all of the changes
	 * that have been recorded
	 * 
	 * @param n
	 * @return
	 */
	public static ArrayList<StateChange> getNLastChanges(int n){
		if(n > gameStateChanges.size())
			n = gameStateChanges.size();
		
		ArrayList<StateChange> changes = new ArrayList<StateChange>();
		Iterator<StateChange> it = gameStateChanges.listIterator(gameStateChanges.size() - n);
		
		while(it.hasNext()){
			changes.add(it.next());
		}	
		return changes;
	}
	
	/**
	 * Returns the iterator that allows for the game state changes
	 * to be followed from the beginning of the game
	 * @return
	 */
	public static Iterator<StateChange> getIterator(){
		return gameStateChanges.iterator();
	}
	
	
	/**
	 * Method groups all stateChanges into players turns
	 * Thus, if player moved their armies and kept attacking several times
	 * the method will group all of this moves into one turn
	 * 
	 * @return
	 */
	public static ArrayList<ArrayList<StateChange>> getPlayersTurns(){
		Iterator<StateChange> it = getIterator();
		ArrayList<ArrayList<StateChange>> allMoves = new ArrayList<ArrayList<StateChange>>();
	
		ArrayList<StateChange> moveChanges = new ArrayList<StateChange>();
		Player player = null;
		StateChange change;
		while(it.hasNext()){
			change = it.next();
			if(player == null)
				player = change.getActingPlayer();
			if(player == change.getActingPlayer()){
				moveChanges.add(change);
			}
			else{
				allMoves.add(moveChanges);
				moveChanges = new ArrayList<StateChange>();
				player = change.getActingPlayer();
				moveChanges.add(change);
			}
		}
		
		return allMoves;
	}
	
	public static void printAllChanges(){
		Iterator<StateChange> it = getIterator();
		int i = 1;
		while(it.hasNext()){
			System.out.println("\nCHANGE " + i);
			System.out.println(it.next().toString());
			i++;
		}
	}
	
}
