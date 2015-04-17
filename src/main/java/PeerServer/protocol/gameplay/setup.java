package PeerServer.protocol.gameplay;

/**

 */
public class setup {

	public setup(int payload, int player_id, int ack_id){
		this.payload = payload;
		this.player_id = player_id;
		this.ack_id = ack_id;
	}
	
	public String command = "setup";
	public int payload;
	public int player_id;
	public int ack_id;
	
}
