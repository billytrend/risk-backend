/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by a host at the start of a game, and by all clients in 
 * response to this initial ping. "player_id" may be 
 * null in the case of a non-player host.
 * @author 12001995
 *
 */
public class ping {

	public int player_id;
	public int no_of_players;		//can be null if sent by client 
	
	public ping(int no_of_players, int player_id){
		this.player_id = player_id;
		this.no_of_players = no_of_players;
	}
	
}
