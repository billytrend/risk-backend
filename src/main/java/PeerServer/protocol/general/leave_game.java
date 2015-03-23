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
	
	//used for acknowledgements and leave_game
	int response_codes[][];
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

	
	int response_code; //(4xx) specifying reason for leaving
	boolean receive_updates; //whether user wishes to continue to recieve updates and viewing the game
	int player_id; 

}
