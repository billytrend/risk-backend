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

	int ack_id;
	//response code specifying either agreement or an 
	//error with the recieved command. Deafult is 0 (no error)
	int response; 
}
