package GameState.StateUtils;

/**
 * Created by bt on 30/12/2014.
 */
public class StringifyState {

//    /**
//     * Printing the current state of the game to the console.
//     */
//    public static void print(State state){
//        System.out.println("\nGAME STATE:");
//        System.out.println("------------------------------------");
//        System.out.println("Number of players: " + state.countPlayers());
//        System.out.println("Current player: " + state.getPlayerQueue().getCurrent().id);
//        System.out.println("Territories:");
//        printTerritories(state);
//        System.out.println("\n------------------------------------");
//        System.out.println();
//    }
//
//    private static void printTerritories(State state){
//        Territory ter;
//        String id;
//        for(int i = 0; i < state.countTerritories(); i++){
//            ter = state.getTerritory(i);
//            id = (ter.player == null) ? "no player" : ter.player.id;
//            System.out.print("Ter " + (i + 1) + ": " + id
//                    + "  armies: " + ter.armies.amount + "\t\t");
//            if(i == 1)
//                System.out.println(" ");
//        }
//    }
//
}
