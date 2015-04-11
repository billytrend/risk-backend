/**
 * 
 */
package PeerServer.protocol.dice;

/**
 * Sent by each player in receipt of a “roll” command being received. 
 * The hash should be a string representation of the SHA-256 hash in hexadecimal. 
 * All roll_hash commands must be received by a player before 
 * transmitting its roll_number command, to avoid any player being able to pre-compute the results.
 * @author 120011995
 *
 */
public class roll_hash {
	
	//SHA-256 Hash as a hexadecimal string
	String SHA256_Hash;
	int player_id;
}
