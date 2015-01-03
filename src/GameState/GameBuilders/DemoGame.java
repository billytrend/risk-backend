package GameState.GameBuilders;

import GameState.Player;
import GameState.State;
import GameState.Territory;

/**
 * Created by bt on 30/12/2014.
 */
public class DemoGame {

    public static void buildGame(State state) {

        // creating players
        int numOfPlayers = 5;
        int armiesAtTheStart = 10;
        for(int i = 0; i < numOfPlayers; i++){
            state.addPlayer(
                    new Player(armiesAtTheStart, "state.Player " + (i + 1)));
        }

        // creating territories
        Territory demoLandA = new Territory();
        Territory demoLandB = new Territory();
        Territory demoLandC = new Territory();
        Territory demoLandD = new Territory();

        state.addTerritory(demoLandA);
        state.addTerritory(demoLandB);
        state.addTerritory(demoLandC);
        state.addTerritory(demoLandD);

        //add neighbouring territories to each territory
        state.addBorder(demoLandA, demoLandB);
        state.addBorder(demoLandA, demoLandD);
        state.addBorder(demoLandB, demoLandC);
        state.addBorder(demoLandC, demoLandD);
        state.addBorder(demoLandD, demoLandB);

    }

}
