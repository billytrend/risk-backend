/**
 * 
 */
package PeerServer.protocol.gameplay;

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
public class defend {
	
	public defend(int armies, int player_id, int ack_id){
		this.payload = armies;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "defend";

	//number of armies being used to defend the territory
	public int payload;
	public int player_id;
	public int ack_id;
}
