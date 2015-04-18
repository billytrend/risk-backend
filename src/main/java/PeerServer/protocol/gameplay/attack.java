
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

/**
		{
		
		"command": "attack",
		
		"payload": [1, 2, 2],
		
		"player_id": 0,
		
		"ack_id": 1
		
		}
 *
 *
 */

public class attack {
	
	public attack(int sourceId, int destId, int armies, int playerID, int ack_id){
		payload = new int[]{sourceId, destId, armies};
		this.player_id = playerID;
		this.ack_id = ack_id;
	}

	public attack(int[] payload, int playerID, int ack_id){
		this.payload = payload;
		this.player_id = playerID;
		this.ack_id = ack_id;
	}

	public String command = "attack";
	//source territory ID/destination territory ID/army count triple 
	public int[] payload;
	public int player_id;
	public int ack_id;
}
