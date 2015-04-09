/**
 * 
 */
package PeerServer.protocol.dice;

/**
 * Sent by each player following the receipt of a “roll_hash” being received from all other players. 
 * The payload should correspond to the previously sent hash.
 * @author 120011995
 *
 */
public class roll_number {

	//256 bit random number as hex string
	String random_number;
	int player_id;
}
