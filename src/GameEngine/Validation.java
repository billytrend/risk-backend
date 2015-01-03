//package GameEngine;
//
//import GameState.Events.ValidationResult;
//import GameState.Player;
//import GameState.State;
//import GameState.StateUtils.MapUtils;
//import GameState.Territory;
//
///**
// * Created by bt on 30/12/2014.
// */
//public class Validation {
//
//    private static boolean curPlayerOwnsTerritory(State state, Territory t) {
//        return state.getPlayerQueue().getCurrent().equals(t.getOwner());
//    }
//
//    /**
//     * The method checks whether the given attack is valid and
//     * returns false if it's not valid.
//     *
//     * @param attacker
//     * @param defender
//     * @return
//     */
//    private static ValidationResult checkAttack(State state, Territory attacker, Territory defender){
//
//        if (!MapUtils.areNeighbours(state, attacker, defender)) {
//            return new ValidationResult(false, "TERRITORIES ARE NOT NEIGHBOURS");
//        }
//
//        else if (!curPlayerOwnsTerritory(state, attacker)) {
//            return new ValidationResult(false, "CURRENT PLAYER DOESN'T OWN THIS TERRITORY!");
//        }
//
//        else if (attacker.getArmies().getAmount() <= 1) {
//            return new ValidationResult(false, "YOU DON'T HAVE ENOUGH ARMIES");
//        }
//
//        else if (curPlayerOwnsTerritory(state, defender)) {
//            return new ValidationResult(false, "CAN'T ATTACK OWN TERRITORY");
//        }
//
//        else if (attacker.getArmies().getAmount() < 2) {
//            return new ValidationResult(false, "NOT ENOUGH ARMIES TO ATTACK WITH");
//        }
//
//        return new ValidationResult(true);
//
//    }
//
//    /**
//     * The method used after a territory was taken and the attacker
//     * decided how many armies they want to move to the newly
//     * obtained territory. It checks whether they specified
//     * a sufficient number of armies to move and not more
//     * than they actually have.
//     *
//     * @param numOfArmies
//     * @param numOfDice
//     * @param territory
//     * @return
//     */
//    static ValidationResult checkMoveArmies(State state, Territory source, Territory target, int n){
//
//        if (!curPlayerOwnsTerritory(state, source) || !curPlayerOwnsTerritory(state, target)) {
//            return new ValidationResult(false, "YOU DON'T OWN ONE OF THESE COUNTRIES");
//        }
//
//        if(source.getArmies().getAmount() < n - 1){
//            return new ValidationResult(false, "YOU DON'T HAVE ENOUGH ARMIES ON YOUR SOURCE TERRITORY");
//        }
//
//        return new ValidationResult(true);
//    }
//
//    static ValidationResult checkAttackDice(Territory attacker, int n) {
//
//        if (n > 3) {
//            return new ValidationResult(false, "TOO MANY DICE!");
//        }
//
//        else if (n >= attacker.getArmies().getAmount()) {
//            return new ValidationResult(false, "MUST HAVE N+1 ATTACKERS!");
//        }
//
//        return new ValidationResult(true);
//
//    }
//
//    static ValidationResult checkDefendDice(Territory defender, int n) {
//
//        if (n > 2) {
//            return new ValidationResult(false, "TOO MANY DICE!");
//        }
//
//        else if (n > defender.getArmies().getAmount()) {
//            return new ValidationResult(false, "MUST HAVE N DEFENDERS!");
//        }
//
//        return new ValidationResult(true);
//
//    }
//
//}
