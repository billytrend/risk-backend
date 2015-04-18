package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;


import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * Used for a very simple AI acting as a player
 *
 */
public class DumbBotInterface implements PlayerInterface {

    Random ran = new Random();
	private static Scanner scanner;
	private BlockingQueue<Entry<Object, RequestReason>> connectorQueue;
	private ProtocolConnector connector;
	
    
	// the protocol needs to call this method so that
	// the locals players responses can be parsed
    public void createResponse(){
    	//connector.generateNextResponse();
    }
	
	public DumbBotInterface(BlockingQueue sharedQueue, int id){
		// create a queue to connect with connector
		connectorQueue = new LinkedBlockingDeque<Entry<Object, RequestReason>>();
		
		// create a connector to connect with protocol
		connector = new ProtocolConnector(connectorQueue, sharedQueue, id);
		connector.run();
	}
	
	public DumbBotInterface(){
		connectorQueue = null;
		connector = null;
	}
    
	protected void emit(Player p, String message) {
//        debug("[" + p.getId() + "]" + "\t\t");
//        debug(message);
    }

    /**
     * Getting an integer from the user
     * @return
     */
    protected static int easyIn() {
        // ADDITIONAL CHECKS?
        int a;
        debug("Please enter your selection: ");
        scanner = new Scanner(System.in);
		a = scanner.nextInt();
        return a;
    }
    

    /**
     * 
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason) {
        emit(player, " how many dice do you want to throw? Max " + max);
        emit(player, "Chose " + max);
        
        
    	// notify connector which can later respond to the protocol
    	if(connectorQueue != null){
	        try {
	    
				connectorQueue.put(new MyEntry(Integer.valueOf(max), reason));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return max;
    }

    /**
     * 
     */
    public Territory getTerritory(Player player, HashSet<Territory> possibles,
                                         boolean canResign, RequestReason reason) {
    	
        ArrayList<Territory> posList = new ArrayList<Territory>(possibles);

        String out = "Please choose a territory";
//        switch(reason){
//        case ATTACK_CHOICE:
//        	out += " to attack";
//        	break;
//        default: 
//        	out += " to place an army on";
//
//        }
        emit(player, out);
	
       
        // the player can decide not to make a choice
        // in case of starting an attack or moving armies
        if(canResign){
        	emit(player, "\t0. Don't choose");
        }

        for(int i = 0; i < possibles.size(); i++) {
            emit(player,  "\t" + (i + 1) + ". " + posList.get(i).getId());
        }
        
        // random choice
        if (ran.nextInt(10) == 0 && canResign) {
            return null;
        }

        int toReturn = ran.nextInt(posList.size());
        
    	if(connectorQueue != null){
	    	// notify connector which can later respond to the protocol
	        try {
				connectorQueue.put(new MyEntry(Integer.valueOf(toReturn), reason));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return posList.get(toReturn);

    }
    
    /**
     * 
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        emit(player, "How many armies would you like to move? Max " + max);
        emit(player, "Chose " + max);
        
        int toReturn = ran.nextInt(max + 1);

    	if(connectorQueue != null){
	    	// notify connector which can later respond to the protocol
	        try {
	        	connectorQueue.put(new MyEntry(from, reason));
	        	connectorQueue.put(new MyEntry(to, reason));
	        	connectorQueue.put(new MyEntry(Integer.valueOf(toReturn), reason));
	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return toReturn;
    }
    
    // TODO: you have to play cards if you have more than 5

    @Override
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
    	Triplet<Card, Card , Card> choice;
    	if(possibleCombinations.size() == 0)
        	choice = null;
        else
        	choice = possibleCombinations.get(0);

    	// notify connector which can later respond to the protocol
    	if(connectorQueue != null){
	    	try {
	  			connectorQueue.put(new MyEntry(choice, null));
	  		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
  		return choice;
    }
    
    @Override
    public void reportStateChange(Change change) {

    }



}
