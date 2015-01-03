package PlayerInput.PlayerChoice;

import GameState.Territory;

/**
 * Created by bt on 29/12/2014.
 */
public class CountrySelection extends Choice {

    public Territory country;

    public CountrySelection(Territory country) {
        this.country = country;
    }

    public Territory getCountry() {
        return this.country;
    }
}
