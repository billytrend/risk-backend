/**
 * 
 */
package PeerServer.protocol.cards;

/**
 * Sent by a player during a turn after their final “attack” has been completed, 
 * if any territories have been claimed.
 * @author 120011995
 *
 */
public class draw_card {

	int card_id; //id of the card being drawn
	int player_id;
	int ack_id;
}
