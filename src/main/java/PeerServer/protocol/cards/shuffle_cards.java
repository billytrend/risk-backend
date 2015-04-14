package PeerServer.protocol.cards;

import PeerServer.protocol.dice.Die;

public class shuffle_cards {
	
	//perform dice roll the number of sides of the dice must be the number of cards in the game.
	//*does dice roll* 
	
	
	//The initial card order must be the IDs in ascending order. Cards are IDed by the territory they represent

	public void shuffle(int [] cards){
		//cards: Array containing all cards.
		int len = cards.length;
		for(int i=0; i < len; i++){
		// Swaps cards at deterministically random indices.
		//swap values of cards[i] and cards[generateByte() % len];
		}
	}
	
}
