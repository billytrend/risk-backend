/**
 * 
 */
package PeerServer.protocol.gameplay;

/**
 * Sent by a player at the start of their turn after the “play_cards” command. 
 * Deploys newly gained armies to one or more owned territories. 
 * May also be sent after a successful capture, if the capture results 
 * in six or more cards being held by the player.
 * @author 120011995
 *
 */
public class deploy {
	
	public deploy(int[][] payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "deploy";
	//2D array of territory_id / army count pairs 
	public int payload[][];
	public int player_id;
	public int ack_id;
}
