package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;
import PeerServer.protocol.gameplay.*;
import org.hamcrest.internal.ArrayIterator;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * {
	"command": "setup",
	"payload": 5,
	"player_id": 0,
	"ack_id": 1
}
 * {
	"command": "deploy",
	"payload": [
		[1, 2],
		[2, 2]
	],
	"player_id": 0,
	"ack_id": 1
}

{
	"command": "play_cards",
	"payload": {
		"cards": [
			[1, 2, 3],
			[4, 5, 6]
		],
		"armies": 3
	},
	"player_id": 0,
	"ack_id": 1
}

 * {
	"command": "attack",
	"payload": [1, 2, 2],
	"player_id": 0,
	"ack_id": 1
}

{
	"command": "defend",
	"payload": 2,
	"player_id": 0,
	"ack_id": 1
}

{
	"command": "attack_capture",
	"payload": [1, 2, 2],
	"player_id": 0,
	"ack_id": 1
}

{
	"command": "fortify",
	"payload": [1, 2, 5],
	"player_id": 0,
	"ack_id": 1
}
 * @author pjc8
 *
 */

public class RemotePlayer implements PlayerInterface  {
	
	//TODO: check for castin errors!
	
	public RemotePlayer(BlockingQueue sharedQueue){
		responses = sharedQueue;
	}
	
    private  BlockingQueue<Object> responses;
    private RequestReason lastReason = null;
    private Object response = null;
    private ArrayIterator it = null;
    private int[] lastDeployChoice;
    
    
	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
		if(reason != lastReason){
			try{
				response = responses.take(); // blocks if needed
				if(it != null)
					it = null;
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		int number;
		switch(reason){
			case ATTACK_CHOICE_DICE:
				if(!(response instanceof attack)){
					System.out.println("BUG in asking for dice");
					return 0;
				}
				number =  ((attack) response).payload[2]; 
				break;
			case DEFEND_CHOICE_DICE:
				if(!(response instanceof defend)){
					System.out.println("BUG in asking for dice");
					return 0;
				}			
				number = ((defend) response).payload;
				break;
			default: 
				System.out.println("A BUG in asking for dice");
				return 0;
		}
		
		if(number > max){
			System.out.println("Illigal dice number");
			return 0;
		}
		return number;
	}
	

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,Territory from, boolean canResign, RequestReason reason) {
		if(lastReason == null){
			lastReason = reason;
			try{
				response = responses.take(); // blocks if needed
				if(it != null)
					it = null;
				
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		int id;
		switch(reason){
		// ATTACK
			case ATTACK_CHOICE_FROM:
				if(!(response instanceof attack)){
					System.out.println("BUG in asking for dice");
					return null;
				}
				id = ((attack) response).payload[0];
				break;
			case ATTACK_CHOICE_TO:
				if(!(response instanceof attack)){
					System.out.println("A BUG in asking for territory");
					return null;
				}			
				id = ((attack) response).payload[1];
				break;
						
		// SETUP
			case PLACING_ARMIES_SET_UP:
				if(!(response instanceof setup)){
					System.out.println("A BUG in asking for territory");
					return null;
				}			
				id = ((setup) response).payload;
				break;
			case PLACING_REMAINING_ARMIES_PHASE:
				if(!(response instanceof setup)){
					System.out.println("A BUG in asking for territory");
					return null;
				}			
				id = ((setup) response).payload;
				break;
				
		// DEPLOY
			case PLACING_ARMIES_PHASE:
				if(!(response instanceof deploy)){
					System.out.println("A BUG in asking for territory");
					return null;
				}	
				if(it == null)
					it = new ArrayIterator(((deploy) response).payload);
				if(it.hasNext()){
					lastDeployChoice = (int[]) it.next();
					id = lastDeployChoice[0];
				}
				else{
					System.out.println("Player didnt place all of his armies");
					return null;
				}
				break;
	
		// ENDING MOVE, PLACNING SOME ARMIES
			case REINFORCEMENT_PHASE:
				if(!(response instanceof fortify)){
					System.out.println("A BUG in asking for territory");
					return null;
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
				System.out.println("A BUG in asking for territory");
				return null;
		}
		
		// return the territory of the specified id
		for(Territory ter : possibles){
			if(ter.getNumeralId() == id)
				return ter;
		}

		// no territory like that found in possibles
		// TODO: handle wrong input nicely -- connect with protocol?
		System.out.println("illegal territory specified");
		return null;

	}

    @Override
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
		if(reason != lastReason){
			try{
				response = responses.take(); // blocks if needed
				if(it != null)
					it = null;
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		int number = 0;
		switch(reason){
		// DEPLOY
			case PLACING_ARMIES_PHASE:
				if(!(response instanceof deploy)){
					System.out.println("BUG getting number of armies");
					return 0;
				}	
				number = lastDeployChoice[1];
				break;
		// ENDING MOVE, PLACNING SOME ARMIES
			case REINFORCEMENT_PHASE:
				if(!(response instanceof fortify)){
					System.out.println("BUG getting number of armies");
					return 0;
				}
				number = ((fortify) response).payload[2];
				break;
			case POST_ATTACK_MOVEMENT:
				if(!(response instanceof attack_capture)){
					System.out.println("BUG getting number of armies");
					return 0;
				}
				// no reinforsments
				number = ((attack_capture) response).payload[2];
				break;
			default:
				System.out.println("A BUG in asking for dice");
				return 0;
		}
		
		if(number > max){
			System.out.println("illegal army amount: " + number + " max: " + max);
			return 0;
		}
		
		return number;
	}

	
	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		return null;
	}

    @Override
    public void reportStateChange(Change change) {
      //  connection.send(Jsonify.getObjectAsJsonString(change));
    }

}
