/**
 * 
 */
package PeerServer.protocol.setup;

import java.util.ArrayList;

import GameState.Player;

/**
 * 
 * @author 120011995
 * Sent by a host to each player after connection as players join the game.
 * Maps player IDs to real names. Optional command, will only be sent 
 * if the player specified a real name itself.
 * 
 * {
    "command": "players_joined",
    "payload": [
        [0, "Player A", ""],
        [1, "Player B", ""]
    ]
}
 * 
 */
public class players_joined {

	public String command = "players_joined";
	//2D array of integer player ID/string name pairs.
	public Object[][] payload;

	public players_joined(ArrayList<Player> allPlayers){
		payload = new Object[allPlayers.size()][3];
		Player current;
		for(int i = 0; i < allPlayers.size(); i++){
			current = allPlayers.get(i);
			payload[i] = new Object[]{(Integer) current.getNumberId(), current.getId(), current.getPublicKey()};
		}
	}

	public players_joined(Object[] strings) {
		payload = new Object[1][3];
		payload[0] = strings;
	}
	
}
