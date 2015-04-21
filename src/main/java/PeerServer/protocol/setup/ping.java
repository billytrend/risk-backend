/**
 * 
 */
package PeerServer.protocol.setup;

import PeerServer.protocol.gameplay_command;

/**
 * Sent by a host at the start of a game, and by all clients in 
 * response to this initial ping. "player_id" may be 
 * null in the case of a non-player host.
 * @author 12001995
 *
 *{
    "command": "ping",
    "payload": 5,
    "player_id": 0
}
 */
public class ping{

	public String command = "ping";
	public Integer payload;		//can be null if sent by client 
	public int player_id;
	
	public ping(Integer no_of_players, int player_id){
		this.payload = no_of_players;
		this.player_id = player_id;
	}
	
}
