/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by a host to a client on receipt of a 
 * “join_game” command, as confirmation of adding 
 * them to the game.
 * @author 120011995
 *
 *
{
    "command": "accept_join_game",
    "payload": {
        "player_id": 1,
        "acknowledgement_timeout": 2,
        "move_timeout": 30
    }
}

 *
 */
public class accept_join_game {
	public String command = "accept_join_game";
	public payload payload;
	
	public accept_join_game(int player_id,int acknowledgement_timeout,int move_timeout){
		this.payload = new payload();
		this.payload.player_id = player_id;
		this.payload.acknowledgement_timeout = acknowledgement_timeout;
		this.payload.move_timeout = move_timeout;
	}
	
	public class payload{
		public int player_id;
		public int acknowledgement_timeout;
		public int move_timeout;
	}
}

