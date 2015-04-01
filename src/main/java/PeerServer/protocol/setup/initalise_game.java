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

	public initalise_game(Integer version, String[] supportedFeatures){
		this.version = version;
		this.supported_features = supportedFeatures;
	}
	
	public String command = "initialize_game";
	public Integer version;
	public String[] supported_features;

}
