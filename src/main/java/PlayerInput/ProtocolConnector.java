package PlayerInput;

import java.util.Iterator;
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
import PeerServer.protocol.gameplay.deploy;
import PeerServer.protocol.gameplay.fortify;
import PeerServer.protocol.gameplay.setup;


public class ProtocolConnector implements Runnable {

	public ProtocolConnector(BlockingQueue responses, BlockingQueue sharedQueue, int id){
		this.protocolQueue = sharedQueue;
		this.responsesQueue = responses;
		this.myID = id;
	}
	
	private BlockingQueue<Entry<Object, RequestReason>> responsesQueue; // gets responses from local player
    private  BlockingQueue<Object> protocolQueue; // sends responses to protocol
    
    private int myID;
    private Random ran = new Random();


    public void run(){
    	while(true){
    		generateNextResponse();
    	}
    }
    
    public void generateNextResponse(){
    	Entry<Object, RequestReason> next = null;
    	
    	try {
			next = responsesQueue.take();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

    	ArrayList<Object> responseParts = new ArrayList<Object>();
        RequestReason servedReason = next.getValue();
    	responseParts.add(next.getKey());
    	
    	appendNewResponseParts(responseParts, servedReason);
    	createNewProtocolCommand(responseParts, servedReason);
    }
    
	/**
     * 
     * @param servedReason 
     * @param responseParts2 
     * @return
     */
    private void appendNewResponseParts(ArrayList<Object> responseParts, RequestReason servedReason) {
        RequestReason currentReason = servedReason;
    	Entry<Object, RequestReason> next = null;

        // get all information with the same request reason
        while(true){        		
        	next = responsesQueue.peek();
        	currentReason = next.getValue();   		
        		
        	// if next reason differ from the current one stop collecting response parts
        	if(currentReason != servedReason){
        		if(servedReason == RequestReason.ATTACK_CHOICE_FROM){
        			if((currentReason != RequestReason.ATTACK_CHOICE_TO) &&
        			(currentReason != RequestReason.ATTACK_CHOICE_DICE))
        				break;
        		}
        		else
        			break;
        	}
        	
        	// TODO: not sure about that, but it should be somewhere!
        	if(responsesQueue.isEmpty())
        		break;
        	
        	// if it was the same, take it from the queue
        	try {
        		next = responsesQueue.take();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        	
        	responseParts.add(next.getKey());
        }
	}

    
    /**
     * 
     * @param responseParts
     * @param servedReason
     */
    private void createNewProtocolCommand(ArrayList<Object> responseParts,
			RequestReason servedReason) {
        if(servedReason == null){
    			createPlayCardsCommand(responseParts);
        }
        else{
	        switch(servedReason){
	        	case PLACING_ARMIES_SET_UP: // asking for an empty territory id (setup)
	        		createSetUpCommand(responseParts);
	        		break;
	        	case PLACING_REMAINING_ARMIES_PHASE:
	        		createSetUpCommand(responseParts); // asking for our territory id (setup)   	
	        		break;
	        	case PLACING_ARMIES_PHASE:
	        		createDeployCommand(responseParts); // asking for a territory id (from) and army amount 
	        		break;
	        	case REINFORCEMENT_PHASE:
	        		createFortifyCommand(responseParts); // asking for two territory ids and army amount
	        		break;
	        	case ATTACK_CHOICE_FROM:
	        		createAttackCommand(responseParts); // asking for two ids and army amount
	        		break;
	        	case POST_ATTACK_MOVEMENT:
	        		createAttackCaptureCommand(responseParts); // asking for two territory ids and army amount
	        		break;
	        	case DEFEND_CHOICE_DICE: // asking for amount of armies / dice
	        		createDefendCommand(responseParts);
	        		break;
	        }
        }
	}


	/**
     * Returns the number of armies / dice got from the player
     * which is stored in the given responses array
     * 
     * @param responses
     * @return
     */
    private Integer parseInteger(ArrayList<Object> responses, boolean territoryId){
     	
    	if(responses.size() != 0)
    		System.out.println("Wrong size of required parts to parse int");
    	if(territoryId){
    		return getId(responses.get(0));
    	}
    	else
    		return (Integer) responses.get(0);
    }
    
    

    /**
     * Returns the array containing ids of two territories and a number
     * specyfying the amount of armies. It gets the information from the responses
     * array which should store two Territory objects and one Integer
     * @param responses
     * @return
     */
    private int[] parseTwoTerritoriesAndArmy(ArrayList<Object> responses){
    	// need two territories and armies
    	if(responses.size() != 3)
    		System.out.println("Wrong size of territory - territory - int parts");
    	
    	Integer idFrom = getId(responses.get(0));	
    	Integer idTo = getId(responses.get(1));
    	Integer armies = (Integer) responses.get(2);
    	
    	int[] payload = new int[3];
    	payload[0] = idFrom;
    	payload[1] = idTo;
    	payload[2] = armies;
    	
    	return payload;
    }
    
    
    
    
    /**
     * returns the id of the given Territory object, if the object given
     * is not an instance of Territory the method returns null
     * @param ob
     * @return
     */
    private Integer getId(Object ob){
    	Territory territory = (ob instanceof Territory) ? (Territory) ob : null;
    	if(territory == null){
    		System.out.println("Error in territory parsing");
    		return null;
    	}
    	
    	int id = territory.getNumeralId();
    	return id;
    }
    
   

    /**
     * Creates a setup command object and add it to the protocol queue
     * to be retrieved by the protocol
     * @param responseParts
     */
    private void createSetUpCommand(ArrayList<Object> responseParts){
    	Integer id = parseInteger(responseParts, true); // get a territory id
    	setup setup = new setup(id, myID, ran.nextInt(50));
    	
    	try {
			protocolQueue.put(setup);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
  
    /**
     * Creates a deploy command and adds it to the protocol queue
     * 
     * @param responseParts
     */
	private void createDeployCommand(ArrayList<Object> responseParts) {
		if((responseParts.size() % 2) != 0){
			System.out.println("Wrong size of response parts in deploy");
		}
		int[][] payload = new int[responseParts.size() / 2][2];
		
		for(int i = 0; i < responseParts.size();){
			Integer idFrom = getId(responseParts.get(i));
	    	Integer armies = (Integer) responseParts.get(i + 1);
	    	
	    	int[] pair = new int[2];
	    	pair[0] = idFrom;
	    	pair[1] = armies;
	    	
	    	payload[i] = pair;
	    	i += 2;
		}
		
		deploy deploy = new deploy(payload, myID, ran.nextInt(50));
		
		try {
			protocolQueue.put(deploy);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
    /**
     * Creates a fortify command and add it to the protocol queue
     * 
     * @param responseParts
     */
    private void createFortifyCommand(ArrayList<Object> responseParts){
    	int[] payload = parseTwoTerritoriesAndArmy(responseParts);
    	fortify fort = new fortify(payload, myID, ran.nextInt(50));
    	
    	try {
			protocolQueue.put(fort);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    /**
     * Creates a defend command and adds it to the protocol queue
     * 
     * @param responses
     */
    private void createDefendCommand(ArrayList<Object> responses){
    	Integer armies = parseInteger(responses, false); // there should be integer, not Territory 
    														// so pass false as param
    	defend def = new defend(armies, myID, ran.nextInt(50));
    	
    	try {
			protocolQueue.put(def);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
    }
    
    
    /**
     * Creates and attack command
     * 
     * @param responses
     */
    private void createAttackCommand(ArrayList<Object> responses){
    	int[] payload = parseTwoTerritoriesAndArmy(responses);
    	attack att = new attack(payload, myID, ran.nextInt(50));
    	
    	try {
			protocolQueue.put(att);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
   
    /**
     * Creates a post attack movement command
     * 
     * @param responses
     */
    private void createAttackCaptureCommand(ArrayList<Object> responses){
    	int[] payload = parseTwoTerritoriesAndArmy(responses);
    	attack_capture att = new attack_capture(payload, myID, ran.nextInt(50));
    
    	try {
			protocolQueue.put(att);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Creares play card command
     * @param responses
     */
    private void createPlayCardsCommand(ArrayList<Object> responses){
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
    
}
