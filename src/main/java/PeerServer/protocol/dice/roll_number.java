/**
 * 
 */
package PeerServer.protocol.dice;

/**
 * Sent by each player following the receipt of a “roll_hash” being received from all other players. 
 * The payload should correspond to the previously sent hash.
 * @author 120011995
 *
 *{

"command": "roll_number",

"payload":

"9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",

"player_id": 0

 */
public class roll_number {

	public roll_number(String payload, int player_id){
		this.payload = payload;
		this.player_id = player_id;
	}
	
	public String command = "roll_number";
	//256 bit random number as hex string
	public String payload;
	public int player_id;
}
