/**
 * 
 */
package PeerServer.protocol.setup;

import PeerServer.protocol.protocol_command;

/**
 * Sent by a host at the start of a game, indicating 
 * all players have responded to the initial ping, or the timeout 
 * has been reached. "player_id" may be null 
 * in the case of a non-player host.
 * @author 120011995
 *{
    "command": "ready",
    "payload": null,
    "player_id": 0,
    "ack_id": 1
}
 */
public class ready extends protocol_command{

	public ready(String payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "ready";
	public Object payload = null;
	public int ack_id;
	
}
