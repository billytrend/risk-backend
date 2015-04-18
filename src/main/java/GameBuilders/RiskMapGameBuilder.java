package GameBuilders;

import GameState.Card;
import GameState.CardType;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.CardUtils;
import GameUtils.ContinentUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class RiskMapGameBuilder {

    private static String[] colours = new String[] {
            "F5A9EE", "A6EDDE", "C5A6ED", "E6F07A", "BEF788"
    };


    public static ArrayList<Player> colourPlayers(ArrayList<Player> ps) {

        // creating players
        for (int i = 0; i < ps.size(); i++) {
            ps.get(i).setColour(colours[i]);
        }
        return ps;

    }

	/*
	 * Builds a very basic game with four AIs playing against each other and
	 * full risk map.
	 */
	public static State buildGame(PlayerInterface[] interfaces) {
        State state = new State();

        if (interfaces != null) {
            // creating players
            ArrayList<Player> ps = new ArrayList<Player>();
            for (int i = 0; i < interfaces.length; i++) {
                Player p = new Player(interfaces[i], i + 1);
                p.setColour(colours[i]);
                ps.add(p);
            }
            state.setPlayers(ps);
        }


        //array unused
      //  String[] countryNames = new String[]{ "eastern_australia","indonesia", "new_guinea", "alaska", "ontario", "northwest_territory", "venezuela", "madagascar", "north_africa", "greenland", "iceland", "great_britain", "scandinavia", "japan", "yakursk", "kamchatka", "siberia", "ural", "afghanistan", "middle_east", "india", "siam", "china", "mongolia", "irkutsk", "ukraine", "southern_europe", "western_europe", "northern_europe", "egypt", "east_africa", "congo", "south_africa", "brazil", "argentina", "eastern_united_states", "western_united_states", "quebec", "central_america", "peru", "western_australia", "alberta"};

        addRiskTerritoriesToState(state);

		return state;

	}


	public static void addRiskTerritoriesToState(State state){
		// creating territories
		//Create territories for North America
		Territory alaska = new Territory("alaska", 0);
		Territory northwestTerritory = new Territory("northwest_territory", 1);
		Territory greenland = new Territory("greenland", 2);
		Territory alberta = new Territory("alberta", 3);
		Territory ontario = new Territory("ontario", 4);
		Territory quebec = new Territory("quebec", 5);
		Territory westernUS = new Territory("western_united_states", 6);
		Territory easternUS = new Territory("eastern_united_states", 7);
		Territory centralAmerica = new Territory("central_america", 8);

		//Create territories for South America
		Territory venezuela = new Territory("venezuela", 9);
		Territory peru = new Territory("peru", 10);
		Territory brazil = new Territory("brazil", 11);
		Territory argentina = new Territory("argentina", 12);

		//Create territories for Europe
		Territory iceland = new Territory("iceland", 13);
		Territory scandinavia = new Territory("scandinavia", 14);
		Territory ukraine = new Territory("ukraine", 15);
		Territory greatBritain = new Territory("great_britain", 16);
		Territory northernEurope = new Territory("northern_europe", 17);
		Territory westernEurope = new Territory("western_europe", 18);
		Territory southernEurope = new Territory("southern_europe", 19);

		//Create territories for Africa
		Territory northAfrica = new Territory("north_africa", 20);
		Territory egypt = new Territory("egypt", 21);
		Territory congo = new Territory("congo", 22);
		Territory eastAfrica = new Territory("east_africa", 23);
		Territory southAfrica = new Territory("south_africa", 24);
		Territory madagascar = new Territory("madagascar", 25);

		//Create territories for Asia
		Territory ural = new Territory("ural", 26);
		Territory siberia = new Territory("siberia", 27);
		Territory yakutsk = new Territory("yakutsk", 28);
		Territory kamchatka = new Territory("kamchatka", 29);
		Territory irkutsk = new Territory("irkutsk", 30);
		Territory mongolia = new Territory("mongolia", 31);
		Territory japan = new Territory("japan", 32);
		Territory afghanistan = new Territory("afghanistan", 33);
		Territory china = new Territory("china", 34);
		Territory middleEast = new Territory("middle_east", 35);
		Territory india = new Territory("india", 36);
		Territory siam = new Territory("siam", 37);

		//Create territories for Australian Archipelago
		Territory indonesia = new Territory("indonesia", 38);
		Territory newGuinea = new Territory("new_guinea", 39);
		Territory westernAustralia = new Territory("western_australia", 40);
		Territory easternAustralia = new Territory("eastern_australia", 41);

        ArrayList<Territory[]> continents = new ArrayList<Territory[]>();
        //Create continent arrays to be passed to ContinentUtils
        Territory[] northAmerica = {alaska, northwestTerritory, greenland, alberta, ontario, quebec, westernUS, easternUS, centralAmerica};
        Territory[] southAmerica = {venezuela, peru, brazil, argentina};
        Territory[] europe = {iceland, scandinavia, ukraine, greatBritain, northernEurope, westernEurope, southernEurope};
        Territory[] africa = {northAfrica, egypt, congo, eastAfrica, southAfrica, madagascar};
        Territory[] asia = {ural, siberia, yakutsk, kamchatka, irkutsk, mongolia, japan, afghanistan, china, middleEast, india, siam};
        Territory[] australia = {indonesia, newGuinea, westernAustralia, easternAustralia};
        continents.add(northAmerica);
        continents.add(southAmerica);
        continents.add(europe);
        continents.add(africa);
        continents.add(asia);
        continents.add(australia);
        //Add territories
        for(Territory[] continent: continents){
            for(Territory territory: continent){
                TerritoryUtils.addTerritory(state, territory);
            }
        }
        //Add continents
        // TODO: AR THESE TEH RIGHT CONTINETN IDS?
        ContinentUtils.addContinent(state, northAmerica, 5, "north_america", 1);
        ContinentUtils.addContinent(state, southAmerica, 3, "south_america", 2);
        ContinentUtils.addContinent(state, europe, 5, "europe", 3);
        ContinentUtils.addContinent(state, africa, 2, "africa" , 4);
        ContinentUtils.addContinent(state, asia, 7, "asia", 5);
        ContinentUtils.addContinent(state, australia, 2, "australia", 6);


        //Add borders
		//Add borders for North America
		TerritoryUtils.addBorder(state, alaska, kamchatka);
		TerritoryUtils.addBorder(state, alaska, northwestTerritory);
		TerritoryUtils.addBorder(state, alaska, alberta);
		TerritoryUtils.addBorder(state, northwestTerritory, greenland);
		TerritoryUtils.addBorder(state, northwestTerritory, alberta);
		TerritoryUtils.addBorder(state, northwestTerritory, ontario);
		TerritoryUtils.addBorder(state, greenland, iceland);
		TerritoryUtils.addBorder(state, greenland, ontario);
		TerritoryUtils.addBorder(state, greenland, quebec);
		TerritoryUtils.addBorder(state, alberta, ontario);
		TerritoryUtils.addBorder(state, alberta, westernUS);
		TerritoryUtils.addBorder(state, ontario, quebec);
		TerritoryUtils.addBorder(state, ontario, westernUS);
		TerritoryUtils.addBorder(state, ontario, easternUS);
		TerritoryUtils.addBorder(state, quebec, easternUS);
		TerritoryUtils.addBorder(state, westernUS, easternUS);
		TerritoryUtils.addBorder(state, westernUS, centralAmerica);
		TerritoryUtils.addBorder(state, easternUS, centralAmerica);
		TerritoryUtils.addBorder(state, centralAmerica, venezuela);

		//Add borders for South America
		TerritoryUtils.addBorder(state, venezuela, peru);
		TerritoryUtils.addBorder(state, venezuela, brazil);
		TerritoryUtils.addBorder(state, peru, brazil);
		TerritoryUtils.addBorder(state, peru, argentina);
		TerritoryUtils.addBorder(state, brazil, northAfrica);
		TerritoryUtils.addBorder(state, brazil, argentina);

		//Add borders for Europe
		TerritoryUtils.addBorder(state, iceland, scandinavia);
		TerritoryUtils.addBorder(state, iceland, greatBritain);
		TerritoryUtils.addBorder(state, greatBritain, scandinavia);
		TerritoryUtils.addBorder(state, greatBritain, northernEurope);
		TerritoryUtils.addBorder(state, greatBritain, westernEurope);
		TerritoryUtils.addBorder(state, westernEurope, northAfrica);
		TerritoryUtils.addBorder(state, westernEurope, northernEurope);
		TerritoryUtils.addBorder(state, westernEurope, southernEurope);
		TerritoryUtils.addBorder(state, scandinavia, northernEurope);
		TerritoryUtils.addBorder(state, scandinavia, ukraine);
		TerritoryUtils.addBorder(state, northernEurope, ukraine);
		TerritoryUtils.addBorder(state, northernEurope, southernEurope);
		TerritoryUtils.addBorder(state, southernEurope, northAfrica);
		TerritoryUtils.addBorder(state, southernEurope, egypt);
		TerritoryUtils.addBorder(state, southernEurope, middleEast);
		TerritoryUtils.addBorder(state, southernEurope, ukraine);
		TerritoryUtils.addBorder(state, ukraine, ural);
		TerritoryUtils.addBorder(state, ukraine, afghanistan);
		TerritoryUtils.addBorder(state, ukraine, middleEast);

		//Add borders for Africa
		TerritoryUtils.addBorder(state, northAfrica, egypt);
		TerritoryUtils.addBorder(state, northAfrica, eastAfrica);
		TerritoryUtils.addBorder(state, northAfrica, congo);
		TerritoryUtils.addBorder(state, egypt, middleEast);
		TerritoryUtils.addBorder(state, egypt, eastAfrica);
		TerritoryUtils.addBorder(state, congo, eastAfrica);
		TerritoryUtils.addBorder(state, congo, southAfrica);
		TerritoryUtils.addBorder(state, eastAfrica, southAfrica);
		TerritoryUtils.addBorder(state, eastAfrica, middleEast);
		TerritoryUtils.addBorder(state, eastAfrica, madagascar);
		TerritoryUtils.addBorder(state, southAfrica, madagascar);

		//Add borders for Asia
		TerritoryUtils.addBorder(state, ural, siberia);
		TerritoryUtils.addBorder(state, ural, china);
		TerritoryUtils.addBorder(state, ural, afghanistan);
		TerritoryUtils.addBorder(state, siberia, yakutsk);
		TerritoryUtils.addBorder(state, siberia, irkutsk);
		TerritoryUtils.addBorder(state, siberia, mongolia);
		TerritoryUtils.addBorder(state, siberia, china);
		TerritoryUtils.addBorder(state, yakutsk, kamchatka);
		TerritoryUtils.addBorder(state, yakutsk, irkutsk);
		TerritoryUtils.addBorder(state, irkutsk, kamchatka);
		TerritoryUtils.addBorder(state, irkutsk,mongolia);
		TerritoryUtils.addBorder(state, mongolia, japan);
		TerritoryUtils.addBorder(state, mongolia, kamchatka);
		TerritoryUtils.addBorder(state, mongolia, china);
		TerritoryUtils.addBorder(state, kamchatka, japan);
		TerritoryUtils.addBorder(state, afghanistan, middleEast);
		TerritoryUtils.addBorder(state, afghanistan, india);
		TerritoryUtils.addBorder(state, afghanistan, china);
		TerritoryUtils.addBorder(state, china, india);
		TerritoryUtils.addBorder(state, china, siam);
		TerritoryUtils.addBorder(state, middleEast, india);
		TerritoryUtils.addBorder(state, india, siam);
		TerritoryUtils.addBorder(state, siam, indonesia);

		//Add borders for Australian Archipelago
		TerritoryUtils.addBorder(state, indonesia, newGuinea);
		TerritoryUtils.addBorder(state, indonesia, westernAustralia);
		TerritoryUtils.addBorder(state, indonesia, newGuinea);
		TerritoryUtils.addBorder(state, newGuinea, westernAustralia);
		TerritoryUtils.addBorder(state, newGuinea, easternAustralia);
		TerritoryUtils.addBorder(state, westernAustralia, easternAustralia);


        try {
					createCardDeck(state);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}
	
	
	public static void createCardDeck(State state) throws FileNotFoundException{
		ArrayList<String> t = state.getTerritoryIds();
		File cards = new File("cards");
		Scanner scanner = new Scanner(cards);
		while(scanner.hasNextLine()){
			String[] values = scanner.nextLine().split(":|,|\"");
			int territory = Integer.parseInt(values[0]);
			int cardType = Integer.parseInt(values[1]);
			CardType type = CardType.SOLDIER;
			switch (cardType) {
			case 0:
				type = CardType.SOLDIER;
				break;
			case 1:
				type = CardType.HORSE;
				break;
			case 2:
				type = CardType.CANNON;
			}
			CardUtils.addCard(state, new Card(TerritoryUtils.getTerritoryByName(state, t.get(territory)), type));
		}
		CardUtils.addCard(state, new Card(null, CardType.WILD));
		CardUtils.addCard(state, new Card(null, CardType.WILD));
		scanner.close();
	}

	/*
	 * public static State buildTestGame(){
	 * 
	 * // creating players ArrayList<Player> ps = new ArrayList<Player>(); //
	 * players dont need to have interfaces ps.add(new Player(null, 5, 1));
	 * ps.add(new Player(null, 4, 2));
	 * 
	 * State gameState = new State(ps);
	 * 
	 * Territory demoLandA = new Territory("land1"); Territory demoLandB = new
	 * Territory("land2");
	 * 
	 * TerritoryUtils.addTerritory(gameState, demoLandA);
	 * TerritoryUtils.addTerritory(gameState, demoLandB);
	 * TerritoryUtils.addBorder(gameState, demoLandA, demoLandB);
	 * 
	 * return gameState; }
	 */

	/*
	 * Complicated method that should be simplified but I thought it might be
	 * cool to have it (for tests?) It can create any kind of state, given the
	 * number of players and territories. Neighbouring territories are set at
	 * random.
	 */
	public static State buildTestGame(int numOfPlayers, int armiesAtTheStart,
			int numOfTerritories) {

		// creating players
		ArrayList<Player> ps = new ArrayList<Player>();
		for (int i = 0; i < numOfPlayers; i++) {
			ps.add(new Player(new DumbBotInterface(), i + 1));
		}
		State state = new State();
		state.setPlayers(ps);

		ArrayList<Territory> territories = new ArrayList<Territory>();
		// creating the specified number of territories
		for (int i = 0; i < numOfTerritories; i++) {
			Territory ter = new Territory("country " + (i + 1), i + 1);
			territories.add(ter);
			TerritoryUtils.addTerritory(state, ter);
		}

		Random ran = new Random();
		// add neighbouring territories to each territory
		for (int i = 0; i < numOfTerritories; i++) {

			// a territory can be neigbours with min 1 country
			// and max numOfTerritories countries
			Territory ter = territories.get(i);
			int numOfNeighbours = TerritoryUtils.getNeighbours(state, ter)
					.size();
			int numOfAdditionalNeighbours = (numOfNeighbours != numOfTerritories - 1) ? (ran
					.nextInt(numOfTerritories - 1 - numOfNeighbours) + 1) : 0;

			for (int j = 0; j < numOfAdditionalNeighbours; j++) {

				// randomly get a neighbour...
				Territory neighbour;
				do {
					// need for this messy arithmetic since random can also
					// return 0 which we dont want...
					int randomIndexOfNeighbour = (i + (ran
							.nextInt(numOfTerritories - 1) + 1))
							% numOfTerritories;
					neighbour = territories.get(randomIndexOfNeighbour);
				} while (TerritoryUtils.areNeighbours(state, ter, neighbour));

				TerritoryUtils.addBorder(state, ter, neighbour);
			}

		}
		return state;
	}
}
