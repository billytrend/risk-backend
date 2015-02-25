package GameUtils.Results;

import java.util.ArrayList;
import java.util.Iterator;

import GameState.Player;


public class StateChangeRecord {
	
	private ArrayList<Change> gameStateChanges;
	private Change lastChange;
		
	public StateChangeRecord(){
		gameStateChanges = new ArrayList<Change>();
	}
	
	/**
	 * Adds a given state change to the record of all changes
	 * 
	 * @param change
	 */
	public  void addStateChange(Change change){
		gameStateChanges.add(change);
		lastChange = change;
	}
	
	/**
	 * Returns the last change that was added
	 * @return
	 */
	public Change getLastChange(){
		return lastChange;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSize(){
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
	public ArrayList<Change> getNLastChanges(int n){
		if(n > gameStateChanges.size())
			n = gameStateChanges.size();
		
		ArrayList<Change> changes = new ArrayList<Change>();
		Iterator<Change> it = gameStateChanges.listIterator(gameStateChanges.size() - n);
		
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
	public Iterator<Change> getIterator(){
		return gameStateChanges.iterator();
	}
	
	
	/**
	 * Method groups all stateChanges into players turns
	 * Thus, if player moved their armies and kept attacking several times
	 * the method will group all of this moves into one turn
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Change>> getPlayersTurns(){
		Iterator<Change> it = getIterator();
		ArrayList<ArrayList<Change>> allMoves = new ArrayList<ArrayList<Change>>();
	
		ArrayList<Change> moveChanges = new ArrayList<Change>();
		Player player = null;
		Change change;
		while(it.hasNext()){
			change = it.next();
			if(player == null)
				player = change.getActingPlayer();
			if(player == change.getActingPlayer()){
				moveChanges.add(change);
			}
			else{
				allMoves.add(moveChanges);
				moveChanges = new ArrayList<Change>();
				player = change.getActingPlayer();
				moveChanges.add(change);
			}
		}
		
		return allMoves;
	}
	
	
	public void printAllChanges(){
		Iterator<Change> it = getIterator();
		int i = 1;
		while(it.hasNext()){
			System.out.println("\nSTATE CHANGE " + i);
			System.out.println(it.next().toString());
			i++;
		}
	}
	
}
