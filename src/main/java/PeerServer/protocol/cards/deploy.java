/**
 * 
 */
package PeerServer.protocol.cards;

/**
 * Sent by a player at the start of their turn after the “play_cards” command. 
 * Deploys newly gained armies to one or more owned territories. 
 * May also be sent after a successful capture, if the capture results 
 * in six or more cards being held by the player.
 * @author 120011995
 *
 */
public class deploy {
	
	//2D array of territory_id / army count pairs 
	int pairs [][];
	int player_id;
	int ack_id;
}
