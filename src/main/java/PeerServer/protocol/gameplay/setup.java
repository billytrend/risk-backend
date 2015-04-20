package PeerServer.protocol.gameplay;

import PeerServer.protocol.gameplay_command;

/**

 */
public class setup extends gameplay_command {

	public setup(int payload, int player_id, int ack_id){
		super(player_id);
		this.payload = payload;
		this.ack_id = ack_id;
	}
	
	public String command = "setup";
	public int payload;
	public int ack_id;
	
}
