package GameEngine.PlayerChoice;

import GameState.Territory;

public class CountrySelection extends Choice {

    private Territory country;
    private int numberOfArmies = 1;

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