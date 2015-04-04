/**
 * 
 */
package PeerServer.protocol.general;

/**
 *  Sent by each player in response to any command 
 *  specifying an “ack_id” being received.
 * @author 12001995
 *
 */
public class acknowledgement {

	public acknowledgement(int ack_id, int player_id){
		this.ack_id = ack_id;
		this.player_id = player_id;
	}
	
	public int ack_id;
	//response code specifying either agreement or an 
	//error with the recieved command. Deafult is 0 (no error)
	public int player_id;
}
