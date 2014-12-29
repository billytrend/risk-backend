package GameState;

import java.util.ArrayList;


public class State {

	public static int numOfPlayers;
	public static Map map;
	public static ArrayList<Player> players = new ArrayList<Player>();
	public static int currentPlayer;
	public static boolean endOfGame = false;
	
	/**
	 * Printing the current state of the game to the console.
	 */
	public static void print(){
		System.out.println("\nGAME STATE:");
		System.out.println("------------------------------------");
		System.out.println("Number of players: " + numOfPlayers);
		System.out.println("Current player: " + players.get(currentPlayer).id);
		System.out.println("Territories:");
		printTerritories();
		System.out.println("\n------------------------------------");
		System.out.println();
	}
	
	private static void printTerritories(){
		Territory ter;
		String id;
		for(int i = 0; i < map.territories.size(); i++){
			ter = map.territories.get(i);
			id = (ter.player == null) ? "no player" : ter.player.id;
			System.out.print("Ter " + (i + 1) + ": " + id
					+ "  armies: " + ter.armies.amount + "\t\t");
			if(i == 1)
					System.out.println(" ");
		}
	}
	
	/**
	 * Changes the current player. If all the players
	 * have already took a turn the player who took the first turn
	 * is set to current player.
	 */
	static public void nextPlayer(){
 		if(currentPlayer == numOfPlayers - 1)
			currentPlayer = 0;
		else
			currentPlayer ++;
	}

	/**
	 * Method called when a player wins a territory.
	 * It checks whether the given player holds all the
	 * territories on the map - the end of the game state. 
	 * 
	 * @param player
	 */
	public static void checkWin(Player player){
		// if the player that just won has all the territories on map 
		if(player.territories.size() == map.territories.size()){
			endOfGame = true;
		}
	}
	
}
