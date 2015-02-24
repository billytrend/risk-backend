package GameBuilders;

import java.util.ArrayList;
import java.util.Random;

import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.TerritoryUtils;
import PlayerInput.DumbBotInterface;

public class RiskMapGameBuilder {

	/*
	 * Builds a very basic game with four AIs playing against each other and
	 * full risk map.
	 */
	public static State buildGame(int numOfPlayers, int armiesAtTheStart) {

		// creating players
		ArrayList<Player> ps = new ArrayList<Player>();
		for (int i = 0; i < numOfPlayers; i++) {
			ps.add(new Player(new DumbBotInterface(), armiesAtTheStart, i + 1));
		}
		State state = new State(ps);

		// creating territories
		//Create territories for North America
		Territory alaska = new Territory("Alaska");
		Territory northwestTerritory = new Territory("Northwest Territory");
		Territory greenland = new Territory("Greenland");
		Territory alberta = new Territory("Alberta");
		Territory ontario = new Territory("Ontario");
		Territory quebec = new Territory("Quebec");
		Territory westernUS = new Territory("Western US");
		Territory easternUS = new Territory("Eastern US");
		Territory centralAmerica = new Territory("Central America");

		//Create territories for South America
		Territory venezuela = new Territory("Venezuela");
		Territory peru = new Territory("Peru");
		Territory brazil = new Territory("Brazil");
		Territory argentina = new Territory("Argentina");

		//Create territories for Europe
		Territory iceland = new Territory("Iceland");
		Territory scandinavia = new Territory("Scandinavia");
		Territory ukraine = new Territory("Ukraine");
		Territory greatBritain = new Territory("Great Britain");
		Territory northernEurope = new Territory("Northern Europe");
		Territory westernEurope = new Territory("Western Europe");
		Territory southernEurope = new Territory("Southern Europe");

		//Create territories for Africa
		Territory northAfrica = new Territory("North Africa");
		Territory egypt = new Territory("Egypt");
		Territory eastAfrica = new Territory("East Africa");
		Territory congo = new Territory("Congo");
		Territory southAfrica = new Territory("South Africa");
		Territory madagascar = new Territory("Madagascar");

		//Create territories for Asia
		Territory ural = new Territory("Ural");
		Territory siberia = new Territory("Siberia");
		Territory yakutsk = new Territory("Yakutsk");
		Territory kamchatka = new Territory("Kamchatka");
		Territory irkutsk = new Territory("Irkutsk");
		Territory mongolia = new Territory("Mongolia");
		Territory japan = new Territory("Japan");
		Territory afghanistan = new Territory("Afghanistan");
		Territory china = new Territory("China");
		Territory middleEast = new Territory("Middle East");
		Territory india = new Territory("India");
		Territory siam = new Territory("Siam");

		//Create territories for Australian Archipelago
		Territory indonesia = new Territory("Indonesia");
		Territory newGuinea = new Territory("New Guinea");
		Territory westernAustralia = new Territory("Western Australia");
		Territory easternAustralia = new Territory("Eastern Australia");

		//Add Territories
		//Add territories for North America
		TerritoryUtils.addTerritory(state, alaska);
		TerritoryUtils.addTerritory(state, northwestTerritory);
		TerritoryUtils.addTerritory(state, greenland);
		TerritoryUtils.addTerritory(state, alberta);
		TerritoryUtils.addTerritory(state, ontario);
		TerritoryUtils.addTerritory(state, quebec);
		TerritoryUtils.addTerritory(state, westernUS);
		TerritoryUtils.addTerritory(state, easternUS);
		TerritoryUtils.addTerritory(state, centralAmerica);

		//Add territories for South America
		TerritoryUtils.addTerritory(state, venezuela);
		TerritoryUtils.addTerritory(state, peru);
		TerritoryUtils.addTerritory(state, brazil);
		TerritoryUtils.addTerritory(state, argentina);

		//Add territories for Europe
		TerritoryUtils.addTerritory(state, iceland);
		TerritoryUtils.addTerritory(state, scandinavia);
		TerritoryUtils.addTerritory(state, ukraine);
		TerritoryUtils.addTerritory(state, greatBritain);
		TerritoryUtils.addTerritory(state, northernEurope);
		TerritoryUtils.addTerritory(state, westernEurope);
		TerritoryUtils.addTerritory(state, southernEurope);

		//Add territories for Africa
		TerritoryUtils.addTerritory(state, northAfrica);
		TerritoryUtils.addTerritory(state, egypt);
		TerritoryUtils.addTerritory(state, congo);
		TerritoryUtils.addTerritory(state, eastAfrica);
		TerritoryUtils.addTerritory(state, southAfrica);
		TerritoryUtils.addTerritory(state, madagascar);

		//Add territories for Asia
		TerritoryUtils.addTerritory(state, ural);
		TerritoryUtils.addTerritory(state, siberia);
		TerritoryUtils.addTerritory(state, yakutsk);
		TerritoryUtils.addTerritory(state, kamchatka);
		TerritoryUtils.addTerritory(state, irkutsk);
		TerritoryUtils.addTerritory(state, mongolia);
		TerritoryUtils.addTerritory(state, japan);
		TerritoryUtils.addTerritory(state, afghanistan);
		TerritoryUtils.addTerritory(state, china);
		TerritoryUtils.addTerritory(state, middleEast);
		TerritoryUtils.addTerritory(state, india);
		TerritoryUtils.addTerritory(state, siam);

		//Add territories for Australian Archipelago
		TerritoryUtils.addTerritory(state, indonesia);
		TerritoryUtils.addTerritory(state, newGuinea);
		TerritoryUtils.addTerritory(state, westernAustralia);
		TerritoryUtils.addTerritory(state, easternAustralia);

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


		return state;

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
			ps.add(new Player(new DumbBotInterface(), armiesAtTheStart, i + 1));
		}
		State state = new State(ps);

		ArrayList<Territory> territories = new ArrayList<Territory>();
		// creating the specified number of territories
		for (int i = 0; i < numOfTerritories; i++) {
			Territory ter = new Territory("country" + (i + 1));
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
