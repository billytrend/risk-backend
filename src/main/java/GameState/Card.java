package GameState;

/**
 * Class representing a card. Each card has
 * a specific type and refers to a specific territory.
 *
 */

enum Types { SOLDIER, HORSE, CANNON }

public class Card {


	private final Types type;
	private final Territory territory;
	private Player owner;
	
	public Card(Types type, Territory territory) {
		this.type = type;
		this.territory = territory;
		owner = null;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}
	
}
