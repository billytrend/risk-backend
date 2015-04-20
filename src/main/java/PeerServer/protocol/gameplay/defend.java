/**
 * 
 */
package PeerServer.protocol.gameplay;

import PeerServer.protocol.gameplay_command;

/**
 *  Sent by the defending player immediately following an “attack” command. 
 *  Specifies the number of armies being used to defend with. 
 *  Army count must be 1 or 2, and the territory must contain 
 *  at least the same number of armies as is being used to defend.
 *	In the case of a territory owned by a neutral player being attacked, 
 *	the defend command will be sent by the host instead, 
 * 	specifying the maximum number of armies to defend.
 * @author 120011995
 *
 */
public class defend  extends gameplay_command{
	
	public defend(int armies, int player_id, int ack_id){
		super(player_id);
		this.payload = armies;
		this.ack_id = ack_id;
	}
	
	public String command = "defend";

	//number of armies being used to defend the territory
	public int payload;
	public int ack_id;
}
