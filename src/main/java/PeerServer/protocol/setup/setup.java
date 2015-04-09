/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by each player in turn at the start of the game to 
 * claim a territory or reinforce an 
 * owned territory (once all have been claimed).
 * @author 120011995
 *
 *{
    "command": "setup",
    "payload": 5,
    "player_id": 0,
    "ack_id": 1
}
 */
public class setup {
	
	public setup(String command, int payload, int player_id, int ack_id){
		this.command = command;
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "setup";
	public int payload;
	public int player_id;
	public int ack_id;

}
