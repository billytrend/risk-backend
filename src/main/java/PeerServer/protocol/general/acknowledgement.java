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
	//response code specifying either agreement or an 
	//error with the recieved command. Deafult is 0 (no error)
	public int player_id;
	
	public acknowledgement(int ack_id, int player_id){
		this.payload = ack_id;
		this.player_id = player_id;
	}

}
