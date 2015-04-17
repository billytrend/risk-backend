package PlayerInput;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import org.hamcrest.internal.ArrayIterator;

import GameEngine.RequestReason;
import GameState.Territory;
import PeerServer.protocol.gameplay.attack;
import PeerServer.protocol.gameplay.attack_capture;
import PeerServer.protocol.gameplay.defend;
import PeerServer.protocol.gameplay.fortify;
import PeerServer.protocol.gameplay.setup;

public class ProtocolConnector {

	public void RemotePlayer(BlockingQueue responses, BlockingQueue sharedQueue, int id){
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
   
    
    private void generateNextResponse(){
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
        		
        		if(currentReason != servedReason)
        			break;
        		
        		try {
        			next = responsesQueue.take();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        	}
    		
        	switch(servedReason){
        		case ATTACK_CHOICE_DICE:
        			
        			break;
        		case DEFEND_CHOICE_DICE:
        			
        			break;
        		case ATTACK_CHOICE_FROM:
        			
        			break;
        		case ATTACK_CHOICE_TO:
        			
        			break;
        		case PLACING_ARMIES_SET_UP:
        			createSetUpResponse(responseParts);
        			break;
        		case PLACING_REMAINING_ARMIES_PHASE:
        			
        			break;
        		case PLACING_ARMIES_PHASE:
        			
        			break;
        		case REINFORCEMENT_PHASE:
        			
        			break;
        		case POST_ATTACK_MOVEMENT:
        			
        			break;
        		
        		// TODO: cards
        	}
    		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
   
    }
    
    // setup -- territory id to place payload
    public void createSetUpResponse(ArrayList<Object> responseParts){
    	
    	if(responseParts.size() != 0)
    		System.out.println("Wrong size of setup parts");
    	
    	Object ob = responseParts.get(0);
    	Integer id = getId(ob);
    	if(id == null){
    		System.out.println("ERROR - createSetUp");
    	}
    	
    	setup setup = new setup(id, myID, ran.nextInt(60));
    	try {
			protocolQueue.put(setup);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
  
    // fortify -- payload[] source id - destination id - armies or null
    public void createFortifyResponse(ArrayList<Object> responseParts){
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
    	
    	fortify fort = new fortify(payload, myID, ran.nextInt(60));
    	protocolQueue.add(fort);
    }
    
    
    //defend: payload - amount of armies to defend with - dice
    public void createDefendResponse(ArrayList<Object> responses){
    	if(responses.size() != 0)
    		System.out.println("Wrong size of defend parts");
    	
    	Object ob = responses.get(0);
    	Integer armies = (ob instanceof Integer) ? (Integer) ob : null;
    	if(armies == null)
    		System.out.println("ERROR - createDefendResponse");
    
    	defend def = new defend(armies, myID, ran.nextInt(50));
    	protocolQueue.add(def);  	
    }
    
    
    public void createAttackResponse(ArrayList<Object> responses){
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
    	protocolQueue.add(att);
    }
    
    
   
    public void createAttackCapture(ArrayList<Object> responses){
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
    	protocolQueue.add(att);
    }
    
    
    public void createPlayCards(ArrayList<Object> responses){
    	
    }
    
    public Integer getId(Object ob){
    	Territory territory = (ob instanceof Territory) ? (Territory) ob : null;
    	if(territory == null){
    		System.out.println("Error in setup response creation.");
    		return null;
    	}
    	
    	int id = territory.getNumeralId();
    	return id;
    }
    

    
    
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
    
    
    
    
    
    
    
    
    
}
