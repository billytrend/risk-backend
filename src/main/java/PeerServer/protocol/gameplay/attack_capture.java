/**
 * 
 */
package PeerServer.protocol.gameplay;

import PeerServer.protocol.protocol_command;

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
public class attack_capture extends protocol_command{

	public attack_capture(int[] payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "attack_capture";
	//source territory ID/destination territory ID/army count triple 
	public int[] payload;
	public int ack_id;
}
