import java.util.ArrayList;


public class GameState {

	static int numOfPlayers;
	static Map map;
	static ArrayList<Player> players = new ArrayList<Player>();
	static int currentPlayer;
	static boolean endOfGame = false;
	
	static void print(){
		System.out.println("\nGAME STATE:");
		System.out.println("------------------------------------");
		System.out.println("Number of players: " + numOfPlayers);
		System.out.println("Current player: " + players.get(currentPlayer).id);
		System.out.println("Territories:");
		printTerritories();
		System.out.println("\n------------------------------------");
		System.out.println();
	}
	
	static void printTerritories(){
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
	
	static public void nextPlayer(){
 		if(currentPlayer == numOfPlayers - 1)
			currentPlayer = 0;
		else
			currentPlayer ++;
	}

	public static void checkWin(Player player){
		// if the player that just won has all the territories on map 
		if(player.territories.size() == map.territories.size()){
			endOfGame = true;
		}
	}
	
}
