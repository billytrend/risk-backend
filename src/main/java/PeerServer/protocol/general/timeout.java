/**
 * 
 */
package PeerServer.protocol.general;

/**
 * Sent by the host when a player does not respond in time. 
 * Can be received at any time.
 * @author 12001995
 *
 */
public class timeout {

	int player_id;
	int ack_id;
}
