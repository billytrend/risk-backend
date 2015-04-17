package PeerServer.server;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import org.hamcrest.internal.ArrayIterator;
import org.javatuples.Triplet;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Territory;
import PeerServer.protocol.cards.play_cards;
import PeerServer.protocol.gameplay.attack;
import PeerServer.protocol.gameplay.attack_capture;
import PeerServer.protocol.gameplay.defend;
import PeerServer.protocol.gameplay.fortify;
import PeerServer.protocol.gameplay.setup;


// INT
// -- dice
// attack choice dice
// defend choice dice
// -- armies
// placing_armies_phase
// reinforcement
// post attack move

// TER
// attack from
// arrach to
// placing armies setup
// placing armies phase
// reinforcement phase

public class ProtocolConnector {

	public ProtocolConnector(BlockingQueue<Entry<Object, RequestReason>> responses, BlockingQueue<Object> sharedQueue, int id){
		this.protocolQueue = sharedQueue;
		this.responsesQueue = responses;
		this.myID = id;
	}
	
	// queue used to communicate with the protocol
	// shared with the protocol instance
    private  BlockingQueue<Object> protocolQueue;
    private int myID;
    private Random ran = new Random();
    
    // queue used to accept all responses from the local
    // player interface
	private BlockingQueue<Entry<Object, RequestReason>> responsesQueue; 

   // private RequestReason lastReason = null;
    private Object response = null;
    private ArrayIterator it = null;
    private int[] lastDeployChoice;
//    private Entry<Object, RequestReason> lastResponse;
   
    
    public void generateNextResponse(){
    	Entry<Object, RequestReason> next;
    	try {
    		next = responsesQueue.take();

        	RequestReason servedReason = next.getValue();
        	RequestReason currentReason = servedReason;
        	
        	// stores all information needed to generate response
        	ArrayList<Object> responseParts = new ArrayList<Object>();
        
        	// get all information with the same request reason
        	while(true){
        		responseParts.add(next.getKey());
        		
        		next = responsesQueue.peek();
        		currentReason = next.getValue();   		
        		
        		if(currentReason != servedReason){
        			if(servedReason == RequestReason.ATTACK_CHOICE_FROM){
        				if((currentReason != RequestReason.ATTACK_CHOICE_TO) &&
        				(currentReason != RequestReason.ATTACK_CHOICE_DICE))
        					break;
        			}
        		}
        		
        		try {
        			next = responsesQueue.take();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        	}
    		
        	if(servedReason == null){
    			createPlayCards(responseParts);
        	}
        	else{
	        	switch(servedReason){
	        		case ATTACK_CHOICE_FROM:
	        			createAttackResponse(responseParts);
	        			break;
	        		case PLACING_ARMIES_SET_UP:
	        			createSetUpResponse(responseParts);
	        			break;
	        		case PLACING_REMAINING_ARMIES_PHASE:
	        			createSetUpResponse(responseParts); // TODO: not 100% sure about this        	
	        			break;
	        		case PLACING_ARMIES_PHASE:
	        			createSetUpResponse(responseParts); // TODO: not sure!!!!
	        			break;
	        		case REINFORCEMENT_PHASE:
	        			createFortifyResponse(responseParts); // TODO: ...
	        			break;
	        		case POST_ATTACK_MOVEMENT:
	        			createAttackCapture(responseParts);
	        			break;
        		}
        	}
    		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
   
    }
    
    // setup -- territory id to place payload
    private void createSetUpResponse(ArrayList<Object> responseParts){
    	
    	if(responseParts.size() != 0)
    		System.out.println("Wrong size of setup parts");
    	
    	Object ob = responseParts.get(0);
    	Integer id = getId(ob);
    	if(id == null){
    		System.out.println("ERROR - createSetUp");
    	}
    	
    	setup setup = new setup(id, myID, ran.nextInt(50));
    
    	try {
			protocolQueue.put(setup);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
  
    // fortify -- payload[] source id - destination id - armies or null
    private void createFortifyResponse(ArrayList<Object> responseParts){
    	// need two territories and armies
    	if(responseParts.size() != 3)
    		System.out.println("Wrong size of fortify parts");
    	
    	Integer idFrom = getId(responseParts.get(0));
    	if(idFrom == null)
    		System.out.println("ERROR - fortify");
    	
    	
    	Integer idTo = getId(responseParts.get(1));
    	if(idTo == null)
    		System.out.println("ERROR - fortify");
    	
    	
    	Object ob = responseParts.get(2);
    	Integer armies = (ob instanceof Integer) ? (Integer) ob : null;
    	if(armies == null)
    		System.out.println("ERROR - fortify");
    	
    	int[] payload = new int[3];
    	payload[0] = idFrom;
    	payload[1] = idTo;
    	payload[2] = armies;
    	
    	fortify fort = new fortify(payload, myID, ran.nextInt(50));
    	
    	try {
			protocolQueue.put(fort);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    //defend: payload - amount of armies to defend with - dice
    private void createDefendResponse(ArrayList<Object> responses){
    	if(responses.size() != 0)
    		System.out.println("Wrong size of defend parts");
    	
    	Object ob = responses.get(0);
    	Integer armies = (ob instanceof Integer) ? (Integer) ob : null;
    	if(armies == null)
    		System.out.println("ERROR - createDefendResponse");
    
    	defend def = new defend(armies, myID, ran.nextInt(50));
    	try {
			protocolQueue.put(def);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
    }
    
    
    private void createAttackResponse(ArrayList<Object> responses){
    	// need two territories and an army
    	if(responses.size() != 3)
    		System.out.println("Wrong size of attack parts");
    	
    	Integer idFrom = getId(responses.get(0));
    	if(idFrom == null)
    		System.out.println("ERROR - attack");
    	
    	
    	Integer idTo = getId(responses.get(1));
    	if(idTo == null)
    		System.out.println("ERROR - attack");
    	
    	
    	Object ob = responses.get(2);
    	Integer armies = (ob instanceof Integer) ? (Integer) ob : null;
    	if(armies == null)
    		System.out.println("ERROR - attack");
    	
    	int[] payload = new int[3];
    	payload[0] = idFrom;
    	payload[1] = idTo;
    	payload[2] = armies;
    	
    	attack att = new attack(payload, myID, ran.nextInt(50));
    	
    	try {
			protocolQueue.put(att);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
   
    private void createAttackCapture(ArrayList<Object> responses){
    	// need two territories and an army
    	if(responses.size() != 3)
    		System.out.println("Wrong size of attack capture parts");
    	
    	Integer idFrom = getId(responses.get(0));
    	if(idFrom == null)
    		System.out.println("ERROR - attack capture");
    	
    	
    	Integer idTo = getId(responses.get(1));
    	if(idTo == null)
    		System.out.println("ERROR - attack capture");
    	
    	
    	Object ob = responses.get(2);
    	Integer armies = (ob instanceof Integer) ? (Integer) ob : null;
    	if(armies == null)
    		System.out.println("ERROR - attack capture");
    	
    	int[] payload = new int[3];
    	payload[0] = idFrom;
    	payload[1] = idTo;
    	payload[2] = armies;
    	
    	attack_capture att = new attack_capture(payload, myID, ran.nextInt(50));
    
    	try {
			protocolQueue.put(att);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    private void createPlayCards(ArrayList<Object> responses){
    	int[][] cards = new int[responses.size()][3];
    	int armies = -1; // TODO: no idea?
    	
    	for(int i = 0; i < responses.size(); i++){
    		Object object = responses.get(i);
    		if(object instanceof Triplet){
    			Triplet<Card, Card, Card> set = (Triplet<Card, Card, Card>) object;
    			int[] cardsIds = new int[3];
    			
    			//TODO: 
    		//	cardsIds[0] = set.getValue0(). getId
    		//	cardsIds[1] = set.getValue1(). getId
    		//	cardsIds[2] = set.getValue2(). getId
    			cards[i] = cardsIds;
    		}
    		else{
    			System.out.println("Error in  createPlayCards");
    		}
    	}
    	
    	play_cards play_cards = new play_cards(cards, armies, myID, ran.nextInt(50));
    	try {
			protocolQueue.put(play_cards);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

    
    private Integer getId(Object ob){
    	Territory territory = (ob instanceof Territory) ? (Territory) ob : null;
    	if(territory == null){
    		System.out.println("Error in territory parsing");
    		return null;
    	}
    	
    	int id = territory.getNumeralId();
    	return id;
    }
    
}
