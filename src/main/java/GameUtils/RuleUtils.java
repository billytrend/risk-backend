//package GameUtils;
//
//import GameState.Continent;
//
//import GameState.Player;
//import GameState.State;
//
//import java.util.ArrayList;
//
//public class RuleUtils {
//
//    /**
//     * Gives a player a number of armies which depends
//     * on the number of their territories and controlled continents.
//     * 
//     * TODO: make sure it follows the actual rules
//     * 
//     * @param state
//     * @param p
//     */
//    public static void doArmyHandout(State state, Player p) {
//        // hand out armies for the following reasons
//        // 1. armies you have
//        int n = PlayerUtils.getNumberOfTerritoriesOwned(p);
//        int totalHandout = n/3;
//        
//        // the number of armies received can never be less than 3
//        if(totalHandout < 3)
//        	totalHandout = 3;
//        
//        // 2. continents you control
//        ArrayList<Continent> continents = PlayerUtils.playerContinents(state, p);
//        for (Continent c : continents) {
//            totalHandout +=  c.getArmyReward();
//        }
//        // TODO: 3. value of cards
//        // TODO: 4.
//        ArmyUtils.givePlayerNArmies(p, totalHandout);
//    }
//  
//
//}
