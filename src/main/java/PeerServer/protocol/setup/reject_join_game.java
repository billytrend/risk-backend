/**
 * 
 */
package PeerServer.protocol.setup;

/**
 * Sent by a host to a client on receipt 
 * of a “join_game” command, as rejection.
 * @author 120011995
 *
 *{
    "command": "reject_join_game",
    "payload": "Game in progress"
}
 */
public class reject_join_game {
	public String command = "reject_join_game";
	public String payload;		//describing reason for rejection
	

	public reject_join_game(String error_message) {
		this.payload = error_message;
	}
	
	public reject_join_game(){
		
	}
	
}
