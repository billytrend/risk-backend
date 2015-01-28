package GameEngine.PlayerChoice;

import GameState.Territory;

/**
 * Contains the information about a country that 
 * was chosen by the user.
 *
 */
public class CountrySelection extends Choice {

    private Territory country;
    private int numberOfArmies = 1; /// TODO: do we need it?

    public CountrySelection(Territory country) {
        this.country = country;
    }
    public Territory getCountry() {
        return this.country;
    }

    public void setNumberOfArmies(int numberOfArmies) {
        this.numberOfArmies = numberOfArmies;
    }
    public int getNumberOfArmies() {
        return numberOfArmies;
    }
    
}
