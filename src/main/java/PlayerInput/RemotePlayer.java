package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;
import PeerServer.server.AbstractProtocol;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

/**
 *
 */

public class RemotePlayer implements PlayerInterface  {
	
	//TODO: check for castin errors!
	
	public RemotePlayer(BlockingQueue<Entry<Integer, RequestReason>>   sharedQueue, Thread thread, AbstractProtocol protocol){
		responses = sharedQueue;
		protocolThread = thread;
		this.protocol = protocol;
	}
	
    private BlockingQueue<Entry<Integer, RequestReason>> responses;  
    private Entry<Integer, RequestReason> responseEntry;
    private Thread protocolThread;
    private AbstractProtocol protocol;
    boolean wrongCommand;

    
    private Integer getResponse(RequestReason reason){
    	while(true){
    		responseEntry = responses.peek();
    		if(responseEntry != null)
    			break;
    	}
    	RequestReason responseReason = responseEntry.getValue();
    	
    	boolean matching = ((responseReason == RequestReason.PLACING_ARMIES_SET_UP) 
    			&& (reason == RequestReason.PLACING_REMAINING_ARMIES_PHASE));


    	
		if(!matching && (responseReason != reason)){
			System.out.println("Request reason doesn't match ");
			System.out.println(responseReason + "... " + reason);
			notifyProtocol();
			return null;
		}
		
		try{
			responseEntry = responses.take(); // blocks if needed
			Integer response = responseEntry.getKey();
			return response;
		}catch(InterruptedException e){
				e.printStackTrace();
		}
		
		
		return null;
    }
    
	@Override
    public int getNumberOfDice(Player currentPlayer, int max, RequestReason reason, Territory attacking, Territory defending) {
		Integer response = getResponse(reason);
		
		if(response == null){
			System.out.println("Wrong response");
			return 0;
		}
		
		System.out.print("\nREMOTE PLAYER: asked for number of dice " + reason.name() + " --- RETURN " + response);

		return response;
	}
	

	@Override
    public Territory getTerritory(Player player, HashSet<Territory> possibles,Territory from,
                                  boolean canResign, RequestReason reason) {

		Integer response = getResponse(reason);
	
		System.out.print("\nREMOTE PLAYER: asked for territory " + reason.name());

		if(response == null){
			System.out.print(" -- RETURN " + response + "\n");
			return null;
		}
		// return the territory of the specified id
		for(Territory ter : possibles){
			if(ter.getNumeralId() == response){
				System.out.print(" -- RETURN " + response + "\n");
				return ter;
			}
		}
		
		System.out.print(" -- HAD A PROBLEM... not returning, got id: " + response + "\n");
		
		// if it got here it means that an illegal territory was specified
		// notify protocol and return
		notifyProtocol();
		return null;
	}

	
	
    @Override
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
    	Integer response = getResponse(reason);
		
		if(response == null){
			System.out.println("Wrong response");
			return 0;
		}
		System.out.print("\nREMOTE PLAYER: asked for armies " + reason.name() + " --- RETURN " + response);

		return response;
	}

	
	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations){
	/*	Integer cardOneId = getResponse(null);
		Integer cardTwoId = getResponse(null);
		Integer cardThreeId = getResponse(null);
		
		if((cardOneId == null) || (cardTwoId == null) || (cardThreeId == null)){
			System.out.println("Wrong cards? Or maybe ok...");
			return null;
		}
		
		// TODO:
		// get card with id 1
		// get card with id 2
		// get card with id 3
		
		Triplet<Card, Card, Card> cardSet = new Triplet<Card, Card, Card>(null, null, null);
		return cardSet;*/
		return null;
	}
	
	
	private void notifyProtocol() {
		protocolThread.interrupt();
		System.out.println("INTERRRUPP!!!!!!");
		synchronized(this){
			protocol.wrongCommand = true;
			notify();
		}
	}

    @Override
    public void reportStateChange(Change change) {

    }


	@Override
	public void createResponse() {
		// TODO Auto-generated method stub
		
	}

}
