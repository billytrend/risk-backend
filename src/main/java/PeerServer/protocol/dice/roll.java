/**
 * 
 */
package PeerServer.protocol.dice;

/**
 * Sent by the host/a player to request a dice roll for a number of dice with a number of faces. 
 * The roll(s) require input from all players to avoid any player being able to influence the game. 
 * "player_id" may be null in the case of a non-player host requesting 
 * a dice roll to determine the starting player.
 * @author 120011995
 *
 */
public class roll {

	int dice_count; //number of dice to roll
	int dice_faces; //number of faces on each die
	int player_id;
}
