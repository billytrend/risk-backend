/**
 * 
 */
package PeerServer.protocol.gameplay;

import PeerServer.protocol.protocol_command;

/**
 * Sent by a player to signify the end of their turn, 
 * optionally fortifying a single territory by 
 * moving a number of armies into it from another.
 * @author 120011995
 *
 */
public class fortify extends protocol_command{

	public String command = "fortify";
	//source territory ID/destination territory ID/army count triple 
	public int[] payload;
	public int ack_id;
	
	
	public fortify(int[] payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
}
