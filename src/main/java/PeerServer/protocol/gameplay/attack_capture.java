/**
 * 
 */
package PeerServer.protocol.gameplay;

/**
 * Sent by an attacking player upon capturing an opposing territory. 
 * Specifies how many armies will be moved into the captured territory. 
 * The player must move at least one army for each dice rolled in the attack, 
 * and at least one army must remain in each territory at all times. 
 * The source and destination territory IDs must match those specified 
 * in the original “attack” command.
 * @author 120011995
 *
 */
public class attack_capture {

	//source territory ID/destination territory ID/army count triple 
	int attack_array [];
	int player_id;
	int ack_id;
}
