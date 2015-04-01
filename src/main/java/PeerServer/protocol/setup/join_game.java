/**
 * 
 */
package PeerServer.protocol.setup;

/**
 *  Sent by a client to a host attempting to join a game. 
 *  First command sent upon opening a socket 
 *  connection to a host.
 * @author 120011995
 *
 */

/**
 * 
 * {
	"command": "join_game",
	"payload": {
		"supported_versions": [1],			//array of floats
		"supported_features": ["custom_map"]
	}
}
 *
 */


public class join_game {
	
	public float supported_versions [];
	public String supported_features [];
	public String name; 
	public String key;

	public join_game(float [] supported_versions, String [] supported_features, String name, String key) {
		this.supported_versions = supported_versions;
		this.supported_features = supported_features;
		this.name = name;
		this.key = key;
	}
	
}
