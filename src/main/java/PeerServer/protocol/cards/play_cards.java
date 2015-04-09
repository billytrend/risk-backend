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
	
	//null (if no cards being traded)
	int cards [][];	//2D array of card IDs grouped into sets of three
	int armies; 	//number of armies the player expects to recieve for trade
	int player_id;
	int ack_id;
	
}
