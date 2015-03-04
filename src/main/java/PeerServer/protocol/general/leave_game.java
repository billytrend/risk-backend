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
	
	int response_code; //(4xx) specifying reason for leaving
	boolean receive_updates; //whether user wishes to continue to recieve updates and viewing the game
	int player_id; 

}
