/**
 * 
 */
package PeerServer.protocol.cards;

import PeerServer.protocol.gameplay_command;

/**
 * Sent by each player at the start of their turn, 
 * specifying group(s) of cards to trade in for armies, 
 * and the number of armies they are expecting to receive. 
 * This command must always be sent at the start of a turn, 
 * even if no cards are being traded.
 * @author 120011995
 *
 */
public class play_cards extends gameplay_command {
	
	public play_cards(int[][] cards, int armies, int id, int ack){
		super(id);

		if(cards == null){
			this.payload = null;
		}
		else{
			this.payload = new payload();
			this.payload.cards = cards;
			this.payload.armies = armies;
		}
	
		ack_id = ack;
	}
	
	public String command = "play_cards";
	public payload payload;
	public int ack_id;
	
	//null (if no cards being traded)
	public class payload{
		public int cards [][];	//2D array of card IDs grouped into sets of three
		public int armies; 	//number of armies the player expects to recieve for trade
	}
}

