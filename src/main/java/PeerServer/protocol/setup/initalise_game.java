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
 *
 *{
    "command": "initialise_game",
    "payload": {
        "version": 1,
        "supported_features": ["custom_map"]
    }
}
 */
public class initalise_game {

	public initalise_game(Integer version, String[] supportedFeatures){
		this.payload = new payload();
		this.payload.version = version;
		this.payload.supported_features = supportedFeatures;
	}
	
	public String command = "initialise_game";
	public payload payload;
	
	public class payload{
		public Integer version;
		public String[] supported_features;
	}

}
