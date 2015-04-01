/**
 * 
 */
package PeerServer.protocol.general;

/**
 * Sent by the host when a player does not respond in time. 
 * Can be received at any time.
 * @author 12001995
 *
 */
public class timeout {
	
	public timeout(int player_id, int ack_id, int payload){
		this.player_id = player_id;
		this.ack_id = ack_id;
		this.payload = payload;
	}
	
	String command = "timeout";
	int player_id;
	int ack_id;
	int payload;
}
