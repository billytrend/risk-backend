/**
 * 
 */
package PeerServer.protocol.dice;

import PeerServer.protocol.protocol_command;

/**
 * Sent by each player in receipt of a “roll” command being received. 
 * The hash should be a string representation of the SHA-256 hash in hexadecimal. 
 * All roll_hash commands must be received by a player before 
 * transmitting its roll_number command, to avoid any player being able to pre-compute the results.
 * @author 120011995
 *
 *{

"command": "roll_hash",

"payload":

"7b3d979ca8330a94fa7e9e1b466d8b99e0bcdea1ec90596c0dcc8d7ef6b4300c",

"player_id": 0

}
 */


public class roll_hash extends protocol_command{
	
	public roll_hash(String payload, int player_id){
		this.payload = payload;
		this.player_id = player_id;
	}
	
	//SHA-256 Hash as a hexadecimal string
	public String command = "roll_hash";
	public String payload;
}
