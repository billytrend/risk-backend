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
	int player_id;
	int acknowledgement_timeout;
	int move_timeout;
}
