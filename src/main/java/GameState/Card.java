package GameState;

/**
 * Class representing a card. Each card has
 * a specific type and refers to a specific territory.
 *
 */

public class Card {


	private final CardType type;
	private final Territory territory;
	private Player owner;
	
	public Card(Territory terr, CardType cardType) {
		this.territory = terr;
		this.type = cardType;
		owner = null;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

	public CardType getType() {
		return type;
	}

	public Territory getTerritory() {
		return territory;
	}
	
}
