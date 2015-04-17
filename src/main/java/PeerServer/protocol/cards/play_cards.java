/**
 * 
 */
package PeerServer.protocol.cards;

/**
 * Sent by each player at the start of their turn, 
 * specifying group(s) of cards to trade in for armies, 
 * and the number of armies they are expecting to receive. 
 * This command must always be sent at the start of a turn, 
 * even if no cards are being traded.
 * @author 120011995
 *
 */
public class play_cards {
	
	public play_cards(int[][] cards, int armies, int id, int ack){
		this.payload = new payload();
		this.payload.cards = cards;
		this.payload.armies = armies;
		player_id = id;
		ack_id = ack;
	}
	
	public payload payload;
	public int player_id;
	public int ack_id;
	
	//null (if no cards being traded)
	public class payload{
		public int cards [][];	//2D array of card IDs grouped into sets of three
		public int armies; 	//number of armies the player expects to recieve for trade
	}
}

