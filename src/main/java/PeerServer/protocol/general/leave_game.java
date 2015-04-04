/**
 * 
 */
package PeerServer.protocol.general;
/**
 * Sent by any player who wishes to leave the game. May be sent at any time during the game. 
 * The payload should specify a reason for leaving (from the list of valid codes below) 
 * and a flag indicating whether the player wishes to continue receiving updates (view the game).
 * @author 120011995
 *
 */
public class leave_game {
	
	/**
	 * 	
		{
		"command": "leave_game",
		"payload": {
					"response": 401,
					"message": "Defeated by player 2",
					"receive_updates": true
		},
		"player_id": 0
		}
	 */

	
	// array of length three of arraysd of length 2
	public String command = "leave_game";
	public payload payload; // contains response, message and receive updated boolean
	public int player_id; 
	
	
	public leave_game(int responseCode, String msg, boolean updates, int id){
		this.payload = new payload();
		payload.response = responseCode;
		payload.message = msg;
		payload.receive_updates = updates;
		this.player_id = id;
	}

	public class payload{
		public int response;
	    public String message;
	    public boolean receive_updates; 
	}
}