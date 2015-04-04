/**
 * 
 */
package PeerServer.protocol.general;


//used for acknowledgements and leave_game
//int response_codes[][];
//0 OK
//
//100-199 Logic Error
//200-299 Protocol Error
//300-399 Network Error
//400-499 Reasons for leaving game
//
//100 - Undefined logic error
//101 - Invalid card combination
//102 - Invalid move
//
//200 - Undefined protocol error
//300 - Undefined network error
//
//400 - Undefined reason
//401 - Removed from board
//402 - State mismatch
//403 - Client error
//404 - Too few players
//405 - Cheat detected


/**
 * Sent by any player who wishes to leave the game. May be sent at any time during the game. 
 * The payload should specify a reason for leaving (from the list of valid codes below) 
 * and a flag indicating whether the player wishes to continue receiving updates (view the game).
 * @author 120011995
 *
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
public class leave_game {
	
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
