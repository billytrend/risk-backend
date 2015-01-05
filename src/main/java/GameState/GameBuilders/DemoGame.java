package GameState.GameBuilders;

import GameState.StateClasses.Player;
import GameState.StateClasses.State;
import GameState.StateUtils.MapUtils;
import GameState.StateClasses.Territory;
import PlayerInput.DumbBotInterface;

import java.util.ArrayList;

public class DemoGame {

    public static State buildGame() {

        // creating players
        int numOfPlayers = 4;
        int armiesAtTheStart = 10;
        ArrayList<Player> ps = new ArrayList<Player>();
        for(int i = 0; i < numOfPlayers; i++){
            ps.add(new Player("Player " + i, new DumbBotInterface(), armiesAtTheStart));
        }
        State state = new State(ps);

        // creating territories
        Territory demoLandA = new Territory("demoland");
        Territory demoLandB = new Territory("egstate");
        Territory demoLandC = new Territory("someplace");
        Territory demoLandD = new Territory("otherplace");

        MapUtils.addTerritory(state, demoLandA);
        MapUtils.addTerritory(state, demoLandB);
        MapUtils.addTerritory(state, demoLandC);
        MapUtils.addTerritory(state, demoLandD);

        //add neighbouring territories to each territory
        MapUtils.addBorder(state, demoLandA, demoLandB);
        MapUtils.addBorder(state, demoLandA, demoLandD);
        MapUtils.addBorder(state, demoLandB, demoLandC);
        MapUtils.addBorder(state, demoLandC, demoLandD);
        MapUtils.addBorder(state, demoLandD, demoLandB);

        return state;

    }

}
