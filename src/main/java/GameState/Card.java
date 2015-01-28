package GameState;

/**
 * Class representing a card. Each card has
 * a specific type and refers to a specific territory.
 *
 */
public class Card {

	enum Types { SOLDIER, HORSE, CANNON};
	private final Types type;
	private final Territory territory;

	public Card(Types type, Territory territory) {
		this.type = type;
		this.territory = territory;
	}

	Player owner;
	
}
