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
 */
public class players_joined {

	//2D array of integer player ID/string name pairs.
	String[][] players;

	public players_joined(ArrayList<Player> allPlayers){
		players = new String[allPlayers.size()][3];
		Player current;
		for(int i = 0; i < allPlayers.size(); i++){
			current = allPlayers.get(i);
			players[i] = new String[]{Integer.toString(current.getNumberId()), current.getId(), "this is key :)"};
		}
	}
	
}
