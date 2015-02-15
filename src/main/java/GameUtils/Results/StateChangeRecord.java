package GameUtils.Results;

import java.util.ArrayList;
import java.util.Iterator;


public class StateChangeRecord {
	
	private static ArrayList<StateChange> gameStateChanges = new ArrayList<StateChange>();
	private static StateChange lastChange;
	
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
	 * Returns an ArrayList of n most recent state changes,
	 * with the first index being the least recent and the
	 * last index being last (most recent) state change
	 * 
	 * @param n
	 * @return
	 */
	public static ArrayList<StateChange> getNLastChanges(int n){
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
	
}
