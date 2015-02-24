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
	 * Builds a very basic game with four AIs playing against each other
	 * and full risk map.
	 */
    public static State buildGame(int numOfPlayers, int armiesAtTheStart) {

        // creating players
        ArrayList<Player> ps = new ArrayList<Player>();
        for(int i = 0; i < numOfPlayers; i++){
            ps.add(new Player(new DumbBotInterface(), armiesAtTheStart, i + 1));
        }
        State state = new State(ps);

        // creating territories
        Territory alaska = new Territory("Alaska");
        Territory northwestTerritory = new Territory("Northwest Territory");
        Territory greenland = new Territory("Greenland");
        Territory alberta = new Territory("Alberta");
        Territory ontario = new Territory("Ontario");
        Territory quebec = new Territory("Quebec");
        Territory westernUS = new Territory("Western US");
        Territory easternUS = new Territory("Eastern US");
        Territory centralAmerica = new Territory("Central America");
        
        Territory venezuela = new Territory("Venezuela");
        Territory peru = new Territory("Peru");
        Territory brazil = new Territory("Brazil");
        Territory argentina = new Territory("Argentina");

        
        Territory iceland = new Territory("Iceland");
        Territory scandinavia = new Territory("Scandinavia");
        Territory ukraine = new Territory("Ukraine");
        Territory greatBritain = new Territory("Great Britain");
        Territory northernEurope = new Territory("Northern Europe");
        Territory westernEurope = new Territory("Western Europe");
        Territory southernEurope = new Territory("Southern Europe");


        Territory northAfrica = new Territory("North Africa");
        Territory egypt = new Territory("Egypt");
        Territory eastAfrica = new Territory("East Africa");
        Territory congo = new Territory("Congo");

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
        
        
        Territory indonesia = new Territory("Indonesia");
        Territory newGuinea = new Territory("New Guinea");
        Territory westernAustralia = new Territory("Western Australia");
        Territory easternAustralia = new Territory("Eastern Australia");

        
        Territory demoLandB = new Territory("egstate");
        Territory demoLandC = new Territory("someplace");
        Territory demoLandD = new Territory("otherplace");
        Territory demoLandA = new Territory("demoland");


        
        TerritoryUtils.addTerritory(state, demoLandA);
        TerritoryUtils.addTerritory(state, demoLandB);
        TerritoryUtils.addTerritory(state, demoLandC);
        TerritoryUtils.addTerritory(state, demoLandD);

        //add neighbouring territories to each territory
        TerritoryUtils.addBorder(state, demoLandA, demoLandB);
        TerritoryUtils.addBorder(state, demoLandA, demoLandD);
        TerritoryUtils.addBorder(state, demoLandB, demoLandC);
        TerritoryUtils.addBorder(state, demoLandC, demoLandD);
        TerritoryUtils.addBorder(state, demoLandD, demoLandB);

        return state;

    }
    
  /*  public static State buildTestGame(){

        // creating players
        ArrayList<Player> ps = new ArrayList<Player>();
        // players dont need to have interfaces
        ps.add(new Player(null, 5, 1));
        ps.add(new Player(null, 4, 2));
        
        State gameState = new State(ps);
        
        Territory demoLandA = new Territory("land1");
        Territory demoLandB = new Territory("land2");
        
        TerritoryUtils.addTerritory(gameState, demoLandA);
        TerritoryUtils.addTerritory(gameState, demoLandB);
        TerritoryUtils.addBorder(gameState, demoLandA, demoLandB);
        
        return gameState;
    }*/
    
    
    /*
     * Complicated method that should be simplified but I thought it might be cool to have it (for tests?)
     * It can create any kind of state, given the number of players and territories.
     * Neighbouring territories are set at random. 
     */
    public static State buildTestGame(int numOfPlayers, int armiesAtTheStart, int numOfTerritories) {

    	        // creating players
    	        ArrayList<Player> ps = new ArrayList<Player>();
    	        for(int i = 0; i < numOfPlayers; i++){
    	            ps.add(new Player(new DumbBotInterface(), armiesAtTheStart, i + 1));
    	        }
    	        State state = new State(ps);
    	        
    	        
    	        ArrayList<Territory> territories = new ArrayList<Territory>();
    	        // creating the specified number of territories
    	        for(int i = 0; i < numOfTerritories; i++){
    	        	Territory ter = new Territory("country" + (i + 1));
    	        	territories.add(ter);
    	        	TerritoryUtils.addTerritory(state, ter);
    	        }
    	        
    	        Random ran = new Random();
    	        //add neighbouring territories to each territory
    	        for(int i = 0; i < numOfTerritories; i++){
    	        	
    	        	// a territory can be neigbours with min 1 country
    	        	// and max numOfTerritories countries
    	        	Territory ter = territories.get(i);
    	        	int numOfNeighbours = TerritoryUtils.getNeighbours(state, ter).size();
    	        	int numOfAdditionalNeighbours = (numOfNeighbours != numOfTerritories - 1) ?
    	        			(ran.nextInt(numOfTerritories - 1 - numOfNeighbours) + 1) : 0;
    	        
    	        	for(int j = 0; j < numOfAdditionalNeighbours; j++){
    	        		
    	        		// randomly get a neighbour...
    	        		Territory neighbour;
    	        		do{
    	        	 		// need for this messy arithmetic since random can also 
    	            		//return 0 which we dont want...
    		        		int randomIndexOfNeighbour = 
    		        				(i + (ran.nextInt(numOfTerritories - 1) + 1)) % numOfTerritories;
    		        		neighbour = territories.get(randomIndexOfNeighbour);
    	        		} while (TerritoryUtils.areNeighbours(state, ter, neighbour));
    	        		
    	        		TerritoryUtils.addBorder(state, ter, neighbour);
    	        	}
    	        
    	        }
    	        return state;
    	    }
  }
