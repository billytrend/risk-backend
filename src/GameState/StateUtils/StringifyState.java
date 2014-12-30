package GameState.StateUtils;

import GameState.Player;
import GameState.PlayerQueue;
import GameState.State;
import GameState.Territory;

/**
 * Created by bt on 30/12/2014.
 */
public class StringifyState {

    /**
     * Printing the current state of the game to the console.
     */
    public static void print(){
        System.out.println("\nGAME STATE:");
        System.out.println("------------------------------------");
        System.out.println("Number of players: " + State.countPlayers());
        System.out.println("Current player: " + State.getPlayerQueue().getCurrent().id);
        System.out.println("Territories:");
        printTerritories();
        System.out.println("\n------------------------------------");
        System.out.println();
    }

    private static void printTerritories(){
        Territory ter;
        String id;
        for(int i = 0; i < State.countTerritores(); i++){
            ter = State.getTerritory(i);
            id = (ter.player == null) ? "no player" : ter.player.id;
            System.out.print("Ter " + (i + 1) + ": " + id
                    + "  armies: " + ter.armies.amount + "\t\t");
            if(i == 1)
                System.out.println(" ");
        }
    }

}
