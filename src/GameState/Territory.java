package GameState;

import java.util.ArrayList;

public class Territory {
	
	public Player player;

	public Armies armies;

	public ArrayList<Territory> neighbours;

	public Territory() {
		// think about it
		player = null;
		armies = new Armies(0);
		neighbours = new ArrayList<Territory>();
	}
	
}
