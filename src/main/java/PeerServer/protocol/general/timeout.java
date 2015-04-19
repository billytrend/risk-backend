/**
 * 
 */
package PeerServer.protocol.general;

import PeerServer.protocol.protocol_command;

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


/**
 * 
 * 
 * {
	"command": "timeout",
	"payload": 2,
	"player_id": 0,
	"ack_id": 1
	}
 *
 */


public class timeout extends protocol_command{
	
	public String command = "timeout";
	public int payload;
	public int ack_id;
	
	public timeout(int payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	
	
}
