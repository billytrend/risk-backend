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
    private final int id;
	
	public Card(Territory terr, CardType cardType, int id) {
		this.territory = terr;
		this.type = cardType;
        this.id = id;
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

    public int getId() {
        return id;
    }
}
