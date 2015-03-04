/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by a host to a client on receipt 
 * of a “join_game” command, as rejection.
 * @author 120011995
 *
 */
public class reject_join_game {

	String error_message;		//describing reason for rejection
}
