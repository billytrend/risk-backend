package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.CardType;
import GameState.Player;
import GameState.Territory;
import GameUtils.CardUtils;
import GameUtils.Results.Change;
import PeerServer.protocol.gameplay.*;

import org.hamcrest.internal.ArrayIterator;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import PeerServer.protocol.cards.*;
import PeerServer.protocol.dice.*;
import PeerServer.protocol.gameplay.*;
import PeerServer.server.AbstractProtocol;

/**
 *
 */

public class RemotePlayer implements PlayerInterface  {
	
	//TODO: check for castin errors!
	
	public RemotePlayer(BlockingQueue sharedQueue, Thread thread, AbstractProtocol protocol){
		responses = sharedQueue;
		protocolThread = thread;
		this.protocol = protocol;
	}
	
    private  BlockingQueue<Object> responses;
    private RequestReason lastReason = null;
    private Object response = null;
    private Iterator iterator = null;
    private int[] lastDeployChoice;
    
    private Thread protocolThread;
    private AbstractProtocol protocol;
    
    boolean gotCardChoices = false;
    boolean wrongCommand;

    
	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		if(reason != lastReason){
			try{
				response = responses.take(); // blocks if needed
				iterator = null; // reset iterator
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		
		int number = 0;
		switch(reason){
			case ATTACK_CHOICE_DICE:
				if(!(response instanceof attack)){
					wrongCommand = true;
				}
				else
					number =  ((attack) response).payload[2]; 
				break;
				
			case DEFEND_CHOICE_DICE:
				if(!(response instanceof defend)){
					wrongCommand = true;
				}
				else
					number = ((defend) response).payload;
				break;
				
			default: 
				wrongCommand = true;
		}
		
		if(number > max){
			wrongCommand = true;
		}
		
		if(wrongCommand){
			System.out.println("A BUG in asking for dice");
			notifyProtocol();
			return 0;
		}
		
		return number;
	}
	

	
	private void notifyProtocol() {
		protocolThread.interrupt();
		
		synchronized(this){
			protocol.wrongCommand = true;
			notify();
		}
	}

	

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles, boolean canResign, RequestReason reason) {
		if(lastReason == null){
			lastReason = reason;
			try{
				response = responses.take(); // blocks if needed
				iterator = null; 
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		int id = 0;
		Territory toReturn;
		switch(reason){
		// ATTACK
			case ATTACK_CHOICE_FROM:
				if(!(response instanceof attack)){
					wrongCommand = true;
				}
				else
					id = ((attack) response).payload[0];
				break;
			case ATTACK_CHOICE_TO:
				if(!(response instanceof attack)){
					wrongCommand = true;
				}
				else
					id = ((attack) response).payload[1];
				break;
						
		// SETUP
			case PLACING_ARMIES_SET_UP:
				if(!(response instanceof setup)){
					wrongCommand = true;
				}			
				else
					id = ((setup) response).payload;
				break;
			case PLACING_REMAINING_ARMIES_PHASE:
				if(!(response instanceof setup)){
					wrongCommand = true;
				}			
				else
					id = ((setup) response).payload;
				break;
				
		// DEPLOY
			case PLACING_ARMIES_PHASE:
				if(!(response instanceof deploy)){
					wrongCommand = true;
				}
				else{
					if(iterator == null)
						iterator = new ArrayIterator(((deploy) response).payload);
					if(iterator.hasNext()){
						lastDeployChoice = (int[]) iterator.next();
						id = lastDeployChoice[0];
					}
					else{
						System.out.println("Player didnt place all of his armies");
						wrongCommand = true;
						toReturn = null;
					}
				}
				break;
	
		// ENDING MOVE, PLACNING SOME ARMIES
			case REINFORCEMENT_PHASE:
				if(!(response instanceof fortify)){
					wrongCommand = true;
				}
				// no reinforsments
				if(((fortify) response).payload == null)
					return null;
				// from
				if(canResign)
					id = ((fortify) response).payload[0];
				// to
				else
					id = ((fortify) response).payload[1];
				break;
			default:
				wrongCommand = true;
		}
		
		if(wrongCommand){
			System.out.println("BUG in asking for territory");
			notifyProtocol();
			return null;
		}
		
		// return the territory of the specified id
		for(Territory ter : possibles){
			if(ter.getNumeralId() == id)
				return ter;
		}

		// if it got here it means that an illegal territory was specified
		// notify protocol and return
		notifyProtocol();
		return null;
	}

	
	
    @Override
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
		if(reason != lastReason){
			try{
				response = responses.take(); // blocks if needed
				iterator = null;
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		int number = 0;
		switch(reason){
		// DEPLOY
			case PLACING_ARMIES_PHASE:
				if(!(response instanceof deploy)){
					wrongCommand = true;
				}	
				number = lastDeployChoice[1];
				break;
		// ENDING MOVE, PLACNING SOME ARMIES
			case REINFORCEMENT_PHASE:
				if(!(response instanceof fortify)){
					wrongCommand = true;
				}
				number = ((fortify) response).payload[2];
				break;
			case POST_ATTACK_MOVEMENT:
				if(!(response instanceof attack_capture)){
					wrongCommand = true;
				}
				// no reinforsments
				number = ((attack_capture) response).payload[2];
				break;
			default:
				wrongCommand = true;

		}
		
		if(number > max){
			wrongCommand = true;
		}
		
		if(wrongCommand){
			System.out.println("BUG getting armies");
			notifyProtocol();
		}
		
		return number;
	}

	
	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations){
	
		// if we don't have any available card choices
		if(!gotCardChoices){
			try{
				response = responses.take(); // blocks if needed
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
			if(!(response instanceof play_cards)){
				wrongCommand = true;
			}
			
			int[][] cards = ((play_cards) response).payload.cards;
			int armies = ((play_cards) response).payload.armies;
			
			if(cards == null)
				return null;
			
			List<Triplet<Card, Card, Card>> cardSets = new ArrayList<Triplet<Card,Card,Card>>();
			
			for(int[] cardSet : cards){
				// TODO: fill this in:
				
				// recognize a card by its id!!!! 
				// get it and append to the triplet
				// add triplet to the array of cardSets above
				
				//	Card a = CardUtils.getCardsOfType(state, cardType);
				//Triplet set = new Triplet<A, B, C>(value0, value1, value2)
			}
			
			// set iterator so remaining cardsets will be returned later
			iterator = cardSets.iterator();
			gotCardChoices = true;
			if(iterator.hasNext()){
				return (Triplet<Card, Card, Card>) iterator.next();
			}
			
			if(wrongCommand){
				System.out.println("BUG getting cards");
				notifyProtocol();
			}
			
			return null;
		}
		
		// if we already parsed card choices return the next one
		else{
			if(iterator.hasNext()){
				return (Triplet<Card, Card, Card>) iterator.next();
			}
			// if there are no more choices, return null
			else{
				gotCardChoices = false;
				return null;
			}
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
