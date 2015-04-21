
package PlayerInput;
import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.CardUtils;
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
public class DumbBotInterfaceProtocol implements PlayerInterface {
	Random ran = new Random();
	private BlockingQueue<Entry<Object, RequestReason>> connectorQueue;
	private Thread connectorThread;
	private Player me;
	
	// the protocol needs to call this method so that
	// the locals players responses can be parsed
	public void createResponse(){
		//connector.generateNextResponse();
	}
	
	
	public DumbBotInterfaceProtocol(BlockingQueue sharedQueue, int id){
		// create a queue to connect with connector
		connectorQueue = new LinkedBlockingDeque<Entry<Object, RequestReason>>();
		// create a connector to connect with protocol
		ProtocolConnector connector = new ProtocolConnector(connectorQueue, sharedQueue, id);
		connectorThread = new Thread(connector);
		connectorThread.start();
	}
	
	public void setPlayer(Player player){
		me = player;
	}
	
	public DumbBotInterfaceProtocol(){
		connectorQueue = null;
		connectorThread = null;
	}
	
	public DumbBotInterfaceProtocol(State a) {
		connectorQueue = null;
		connectorThread = null;
	}
	
	
	protected void emit(Player p, String message) {
		// debug("[" + p.getId() + "]" + "\t\t");
		// debug(message);
	}
	
	
	public int getNumberOfDice(Player currentPlayer, int max, RequestReason reason, Territory attacking, Territory defending) {
		emit(currentPlayer, " how many dice do you want to throw? Max " + max);
		emit(currentPlayer, "Chose " + max);
		// notify connector which can later respond to the protocol
		if(connectorQueue != null){
			try {
				connectorQueue.put(new MyEntry(Integer.valueOf(max), reason));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("DUMB BOT: Asked for dice " + reason.name() + " --- RETURN: " + max + "\n");
		return max;
	}
	/**
	*
	*/
	public Territory getTerritory(Player player, HashSet<Territory> possibles, Territory from,
				boolean canResign, RequestReason reason) {
		ArrayList<Territory> posList = new ArrayList<Territory>(possibles);
		System.out.println("SIZE od possibles: " + posList.size());
		
		Territory chosen = null;
		// random choice
		if (ran.nextInt(10) == 0 && canResign) {
			System.out.println("could resign");
			chosen = null;
		}
		else if(posList.size() == 0){
			chosen = null;
		}
		else{
			int toReturn = ran.nextInt(posList.size());
			chosen = posList.get(toReturn);
		}
		
		Integer chosenId = (chosen == null) ? null : chosen.getNumeralId();
		System.out.println("DUMB BOT: Asked for territory " + reason.name() + " --- RETURN: " + chosenId + "\n");
		
		// need to add another thing (or two) to the queue so that the numbers match
		// this will be ignored later in connector anyway
		if((chosenId == null)){
			if(reason == RequestReason.ATTACK_CHOICE_TO){
				try {
						connectorQueue.put(new MyEntry(chosenId, reason));
						connectorQueue.put(new MyEntry(null, RequestReason.ATTACK_CHOICE_DICE));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(reason == RequestReason.ATTACK_CHOICE_FROM){
				try {
					connectorQueue.put(new MyEntry(chosenId, reason));
					connectorQueue.put(new MyEntry(null, RequestReason.ATTACK_CHOICE_TO));
					connectorQueue.put(new MyEntry(null, RequestReason.ATTACK_CHOICE_DICE));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(reason == RequestReason.REINFORCEMENT_PHASE){
				try {
					// add only twice if there is one before
					if(from != null){
					connectorQueue.put(new MyEntry(null, RequestReason.REINFORCEMENT_PHASE));
					connectorQueue.put(new MyEntry(null, RequestReason.REINFORCEMENT_PHASE));
					}
					else{ // ad three times if there is nothing before and we are resigning
						connectorQueue.put(new MyEntry(null, RequestReason.REINFORCEMENT_PHASE));
						connectorQueue.put(new MyEntry(null, RequestReason.REINFORCEMENT_PHASE));
						connectorQueue.put(new MyEntry(null, RequestReason.REINFORCEMENT_PHASE));
					}
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		}
		else{
			// notify connector which can later respond to the protocol
			try {
				connectorQueue.put(new MyEntry(chosenId, reason));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return chosen;
	}
	
	/**
	*
	*/
	public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
		emit(player, "How many armies would you like to move? Max " + max);
		emit(player, "Chose " + max);
		int toReturn = ran.nextInt(max + 1);
		if((reason == RequestReason.ATTACK_CHOICE_DICE) ||
				(reason == RequestReason.DEFEND_CHOICE_DICE)){
			if(toReturn == 0)
				toReturn = 1;
		}
		// notify connector which can later respond to the protocol
		try {
			if(reason == RequestReason.POST_ATTACK_MOVEMENT){
				System.out.println("Post attack movement");
				connectorQueue.put(new MyEntry(from.getNumeralId(), reason));
				connectorQueue.put(new MyEntry(to.getNumeralId(), reason));
				connectorQueue.put(new MyEntry(toReturn, reason));	
			}
			else{
				connectorQueue.put(new MyEntry(toReturn, reason));	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	
		System.out.println("DUMB BOT: Asked for armies " + reason.name() + " --- RETURN: " + toReturn + "\n");
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
			
		
			try {
				if(choice == null){
					for(int i = 0; i < 4; i++){ // four times null;
						connectorQueue.put(new MyEntry(null, null));
					}
				}
				else{
					int armies = CardUtils.getCurrentArmyPayout(me, choice);
					connectorQueue.put(new MyEntry(((Card)choice.getValue(0)).getId(), null));
					connectorQueue.put(new MyEntry(((Card)choice.getValue(1)).getId(), null));
					connectorQueue.put(new MyEntry(((Card)choice.getValue(2)).getId(), null));
					
					System.out.println("chosen cards");
					System.out.println(((Card)choice.getValue(0)).getId());
					System.out.println(((Card)choice.getValue(1)).getId());
					System.out.println(((Card)choice.getValue(2)).getId());
					
					connectorQueue.put(new MyEntry(armies, null));		
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return choice;
		}
		
	@Override
		public void reportStateChange(Change change) {
	}
}

