package GameState;

/**
 * Class representing one army unit.
 *
 */
public class Army {

	// the territory on which the army resides
	private Territory territory;

	public void setTerritory(Territory territory) {
		this.territory = territory;
	}

	public Territory getTerritory() {
		return territory;
	}
}

