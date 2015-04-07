/**
 * 
 */
package PeerServer.protocol.gameplay;

/**
 * Sent by a player to signify the end of their turn, 
 * optionally fortifying a single territory by 
 * moving a number of armies into it from another.
 * @author 120011995
 *
 */
public class fortify {

	//source territory ID/destination territory ID/army count triple 
	int fortify_array [];
	int player_id;
	int ack_id;
}
