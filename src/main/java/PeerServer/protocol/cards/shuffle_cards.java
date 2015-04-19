package PeerServer.protocol.cards;

import PeerServer.protocol.protocol_command;
import PeerServer.protocol.dice.Die;
import PeerServer.protocol.dice.RandomNumbers;
import PeerServer.protocol.dice.roll_hash;

public class shuffle_cards extends protocol_command{

	
	
	//The initial card order must be the IDs in ascending order. Cards are IDed by the territory they represent

	//TODO: Protocol is confusing on this stuff 
	public void shuffle(int [] cards){
		//perform dice roll the number of sides of the dice must be the number of cards in the game.
		//*does dice roll* 
		
		//RandomNumbers rand = new RandomNumbers(null);
		int len = cards.length;
		for(int i=0; i < len; i++){
		// Swaps cards at deterministically random indices.
			//cards[i] = cards[(rand.getRandomByte() % len)];
		}
	}
}
