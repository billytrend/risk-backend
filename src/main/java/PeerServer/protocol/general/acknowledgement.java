/**
 * 
 */
package PeerServer.protocol.general;

import PeerServer.protocol.gameplay_command;

/**
 *  Sent by each player in response to any command 
 *  specifying an “ack_id” being received.
 * @author 12001995
 *
 *{
    "command": "acknowledgement",
    "payload": 1,
    "player_id": 0
}
 */

public class acknowledgement extends gameplay_command {
	public String command = "acknowledgement";
	public int payload;

	public acknowledgement(int payload, int player_id){
		super(player_id);
		this.payload = payload;
	}
}
