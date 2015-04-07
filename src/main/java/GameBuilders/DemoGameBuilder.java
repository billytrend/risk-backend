package GameBuilders;

import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ContinentUtils;
import GameUtils.TerritoryUtils;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import java.util.ArrayList;
import java.util.Random;

/*
 * Class that sets up a game. Creates several players and the map.
 */
public class DemoGameBuilder {

	/*
	 * Builds a very basic game with four AIs playing against each other
	 * and four territories.
	 */
    public static State buildGame(int armiesAtTheStart, PlayerInterface[] interfaces) {

        // creating players
        ArrayList<Player> ps = new ArrayList<Player>();
        
        for(int i = 0; i < interfaces.length; i++){
            ps.add(new Player(interfaces[i], armiesAtTheStart, i + 1));

        }
        
        State state = new State(ps);

        // creating territories
        Territory demoLandA = new Territory("demoland", 0);
        Territory demoLandB = new Territory("egstate", 1);
        Territory demoLandC = new Territory("someplace", 2);
        Territory demoLandD = new Territory("otherplace", 3);

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
        
        Territory[] contAB = {demoLandA, demoLandB};
        Territory[] contCD = {demoLandC, demoLandD};
        ContinentUtils.addContinent(state, contAB, 4, "demoContAB", 0);
        ContinentUtils.addContinent(state, contCD, 3, "demoContCD", 1);

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
    public static State buildTestGame(int numOfPlayers, int armiesAtTheStart, int numOfTerritories, PlayerInterface[] interfaces) {

       	if(interfaces.length != numOfPlayers){
    		try {
    			throw new Exception();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
       	}
       
    			
    	  // creating players
    	  ArrayList<Player> ps = new ArrayList<Player>();
          for(int i = 0; i < numOfPlayers; i++){
        	  ps.add(new Player(interfaces[i], armiesAtTheStart, i + 1));
    	  }
          State state = new State(ps);
    	        
    	        
    	  ArrayList<Territory> territories = new ArrayList<Territory>();
    	  // creating the specified number of territories
    	  for(int i = 0; i < numOfTerritories; i++){
    		  Territory ter = new Territory("country" + (i + 1), i + 1);
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
    	    		  int randomIndexOfNeighbour = 	(i + (ran.nextInt(numOfTerritories - 1) + 1)) % numOfTerritories;
    	    		  neighbour = territories.get(randomIndexOfNeighbour);
    	    		  } while (TerritoryUtils.areNeighbours(state, ter, neighbour));
    	    	  TerritoryUtils.addBorder(state, ter, neighbour);
    	    	  }
    	      }
    	  return state;
    	  }
    }

