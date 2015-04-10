package GameState;

/**
 * A class representing a territory.
 *
 */
public class Territory {

	private final String id;
	private final int numeralId;

	public Territory(String id, int numId) {
		this.id = id;
		numeralId = numId;
	}

	public String getId() {
		return id;
	}

	public int getNumeralId() {
		return numeralId;
	}

}
