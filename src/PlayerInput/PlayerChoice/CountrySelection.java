package PlayerInput.PlayerChoice;

/**
 * Created by bt on 29/12/2014.
 */
public class CountrySelection extends Choice {

    public String country;

    public CountrySelection(String country) {
        this.country = country;
    }

    public String getCountry() {
        return this.country;
    }
}
