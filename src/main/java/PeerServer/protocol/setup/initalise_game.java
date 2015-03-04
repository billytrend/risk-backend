/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by the host at game start 
 * (after the “ready” command has been acknowledged by all players) 
 * to specify the common protocol version and features to be used.
 * @author 120011995
 *
 */
public class initalise_game {

	//same as join_game?!
	float supported_versions [];
	String supported_features [];
}
