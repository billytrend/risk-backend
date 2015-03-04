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
	
	float supported_versions [];
	String supported_features [];
	
}
