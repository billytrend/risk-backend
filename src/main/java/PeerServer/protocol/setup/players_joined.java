/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * 
 * @author 120011995
 * Sent by a host to each player after connection as players join the game.
 * Maps player IDs to real names. Optional command, will only be sent 
 * if the player specified a real name itself.
 */
public class players_joined {

	//2D array of integer player ID/string name pairs.
	int playerID;
	String playerName;
	
	public players_joined(int playerID, String playerName){
		this.playerID = playerID;
		this.playerName = playerName;
	}
	
}
