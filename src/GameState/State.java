package GameState;

import java.util.ArrayList;


public class State {

	private static ArrayList<Territory> territories = new ArrayList<Territory>();
	private static ArrayList<Player> players = new ArrayList<Player>();
	private static final PlayerQueue playerQueue = new PlayerQueue();

	public static Player getPlayer(int i) {
		return players.get(i);
	}

	public static void addPlayer(Player p) {
		State.players.add(p);
	}

	public static int countPlayers() {
		return players.size();
	}

	public static PlayerQueue getPlayerQueue() {
		return playerQueue;
	}

	public static int countTerritories() {
		return territories.size();
	}

	public static Territory getTerritory(int i) {
		return territories.get(i);
	}

	public static void addTerritory(Territory t) {
		territories.add(t);
	}
}
