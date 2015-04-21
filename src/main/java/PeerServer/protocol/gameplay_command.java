package PeerServer.protocol;

public abstract class gameplay_command {
	
	public gameplay_command(int player_id){
		this.player_id = player_id;
	}
	
	public int player_id;
}
