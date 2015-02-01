package GameBuilders;

import GameState.Player;
import GameState.State;
import GameUtils.TerritoryUtils;
import GameState.Territory;
import PlayerInput.DumbBotInterface;

import java.util.ArrayList;

/*
 * Class that sets up a game. Creates several players and the map.
 */
public class DemoGameBuilder {

	/*
	 * Builds a very basic game with four AIs playing against each other
	 * and four territories.
	 */
    public static State buildGame(int numOfPlayers, int armiesAtTheStart) {

        // creating players
        ArrayList<Player> ps = new ArrayList<Player>();
        for(int i = 0; i < numOfPlayers; i++){
            ps.add(new Player(new DumbBotInterface(), armiesAtTheStart, i + 1));
        }
        State state = new State(ps);

        // creating territories
        Territory demoLandA = new Territory("demoland");
        Territory demoLandB = new Territory("egstate");
        Territory demoLandC = new Territory("someplace");
        Territory demoLandD = new Territory("otherplace");

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
    
    public static State buildTestGame(){

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
    }

}
