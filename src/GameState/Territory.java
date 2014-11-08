import java.util.ArrayList;

public class Territory {
	
	Player player;

	Armies armies;

	ArrayList<Territory> neighbours;

	Territory() {
		// think about it
		player = null;
		armies = new Armies(0);
		neighbours = new ArrayList<Territory>();
	}


}
