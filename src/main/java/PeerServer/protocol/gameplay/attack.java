/**
 * 
 */
package PeerServer.protocol.gameplay;

/**
 * Sent zero or more times by a player during their turn. 
 * Describes an attack from an owned territory to one owned by another player. 
 * Specifies the ID of the territory to attack from, 
 * the ID to attack and the number of armies to attack with.
 * Army count may be 1, 2 or 3, but the source territory must contain 
 * at least one army more than is being used for the attack.
 * @author 120011995
 *
 */
public class attack {

	//source territory ID/destination territory ID/army count triple 
	int attack_array [];
	int player_id;
	int ack_id;
}
