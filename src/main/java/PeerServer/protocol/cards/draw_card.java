/**
 * 
 */
package PeerServer.protocol.cards;

import PeerServer.protocol.gameplay_command;

/**
 * Sent by a player during a turn after their final “attack” has been completed, 
 * if any territories have been claimed.
 * @author 120011995
 *
 */
public class draw_card {

	public int card_id; //id of the card being drawn
	public int ack_id;
}
