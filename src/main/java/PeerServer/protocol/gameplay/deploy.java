/**
 * 
 */
package PeerServer.protocol.gameplay;

import PeerServer.protocol.gameplay_command;


/**
 * Sent by a player at the start of their turn after the “play_cards” command. 
 * Deploys newly gained armies to one or more owned territories. 
 * May also be sent after a successful capture, if the capture results 
 * in six or more cards being held by the player.
 * @author 120011995
 *
 */
public class deploy extends gameplay_command {

	//2D array of territory_id / army count pairs 
	public String command = "deploy";
	public payload payload; 
	public int ack_id;

	public deploy(int pairs [][], int player_id, int ack_id) {
		super(player_id);
		this.payload =  new payload();
		payload.pairs = pairs;
		this.ack_id = ack_id;
	}

	public class payload{
		public int pairs [][];
	}
}
