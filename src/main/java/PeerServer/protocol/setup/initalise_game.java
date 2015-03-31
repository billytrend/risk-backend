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

	public int supported_versions;
	public String supported_features [];
}
