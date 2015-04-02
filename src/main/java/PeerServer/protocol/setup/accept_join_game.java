/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by a host to a client on receipt of a 
 * “join_game” command, as confirmation of adding 
 * them to the game.
 * @author 120011995
 *
 */
public class accept_join_game {
	public int player_id;
	public int acknowledgement_timeout;
	public int move_timeout;
	
	
	public accept_join_game(int player_id,int acknowledgement_timeout,int move_timeout){
		this.player_id = player_id;
		this.acknowledgement_timeout = acknowledgement_timeout;
		this.move_timeout = move_timeout;
	}
}

