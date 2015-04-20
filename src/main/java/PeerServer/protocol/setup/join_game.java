/**
 * 
 */
package PeerServer.protocol.setup;

import PeerServer.protocol.gameplay_command;

/**
 *  Sent by a client to a host attempting to join a game. 
 *  First command sent upon opening a socket 
 *  connection to a host.
 * @author 120011995
 *
 */


/**
 * 
 *{
    "command": "join_game",
    "payload": {
        "supported_versions": [1],
        "supported_features": ["custom_map"],
        "name": "Player 1",
        "public_key": "...",
    }
}
 *
 */

public class join_game{
	public String command = "join_game";
	public payload payload;

	public join_game(Integer[] supported_versions, String[] supported_features, String name, String key) {
		this.payload = new payload();
		this.payload.supported_versions = supported_versions;
		this.payload.supported_features = supported_features;
		this.payload.name = name;
		this.payload.public_key = key;
	}
	
	public join_game(Integer[] supported_versions, String[] supported_features, String name) {
		this.payload = new payload();
		this.payload.supported_versions = supported_versions;
		this.payload.supported_features = supported_features;
		this.payload.name = name;
	}
	public class payload{
		public Integer[] supported_versions;
		public String[] supported_features;
		public String name; 
		public String public_key;
	}
	
}
