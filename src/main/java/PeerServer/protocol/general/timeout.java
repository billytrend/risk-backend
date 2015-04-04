/**
 * 
 */
package PeerServer.protocol.general;

/**
 * Sent by the host when a player does not respond in time. 
 * Can be received at any time.
 * @author 12001995
 *
 *{
    "command": "timeout",
    "payload": 2,
    "player_id": 0,
    "ack_id": 1
}
 */
public class timeout {
	
	public timeout(int player_id, int ack_id, int payload){
		this.player_id = player_id;
		this.ack_id = ack_id;
		this.payload = payload;
	}
	
	public String command = "timeout";
	public int player_id;
	public int ack_id;
	public int payload;
}
