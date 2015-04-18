/**
 * 
 */
package PeerServer.protocol.general;

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

public class acknowledgement {
	public String command = "acknowledgement";
	public int payload;
	public int player_id;

	public acknowledgement(int payload, int player_id){
		this.payload = payload;
		this.player_id = player_id;
	}
}
