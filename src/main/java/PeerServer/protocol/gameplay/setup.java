package PeerServer.protocol.gameplay;

import PeerServer.protocol.protocol_command;

/**

 */
public class setup extends protocol_command {

	public setup(int payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "setup";
	public int payload;
	public int ack_id;
	
}
