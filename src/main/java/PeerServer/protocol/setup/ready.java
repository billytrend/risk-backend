/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by a host at the start of a game, indicating 
 * all players have responded to the initial ping, or the timeout 
 * has been reached. "player_id" may be null 
 * in the case of a non-player host.
 * @author 120011995
 *
 */
public class ready {

	public ready(String payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "ready";
	public String payload = null;
	public int player_id;
	public int ack_id;
	
}
