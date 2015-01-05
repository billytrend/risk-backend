package GameEngine;

import GameState.Events.FightResult;
import GameState.StateClasses.State;
import GameState.StateUtils.StateStats;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Arbitration {

    private static Integer dieThrow(){
        Random ran = new Random();
        Integer result = ran.nextInt(6) + 1;
        return result;
    }

    private static Integer[] nDiceThrow(int numOfDice){
        Integer[] result = new Integer[numOfDice];
        for(int i = 0; i < numOfDice; i++){
            result[i] = dieThrow();
        }
        return result;
    }

    public static void setFirstPlayer(State state) {
        Integer noOfPlayers = StateStats.countPlayers(state);
        Random ran = new Random();
        Integer result = ran.nextInt(noOfPlayers);
        state.getPlayerQueue().setFirstPlayer(result);
    }

    public static FightResult carryOutFight(FightResult result, int dA, int dB) {

        Integer[] attackDice = nDiceThrow(dA);
        Integer[] defendDice = nDiceThrow(dB);

        return arbitrateFight(result, attackDice, defendDice);
    }

    /**
     * The method takes the result of dice throws and compares then
     * to specify how many armies should be taken of each player.
     * The returned value is an array of ints where i[0] is an
     * amount taken of attacker and i[1] is an amount taken of
     * defender
     *
     * @param attacker
     * @param defender
     * @return
     */
    private static FightResult arbitrateFight(FightResult result, Integer[] attacker, Integer[] defender){

        // sort in descending order
        Arrays.sort(attacker, Collections.reverseOrder());
        Arrays.sort(defender, Collections.reverseOrder());

        result.setAttackDiceRolled(attacker);
        result.setDefendDiceRolled(defender);

        // the amount compared is the smaller number of dice
        // that were thrown
        int numberToCompare = (attacker.length > defender.length) ? defender.length : attacker.length;

        for(int i = 1; i <= numberToCompare; i++){

            if(attacker[i] > defender[i]) {
                result.addDefendLoss();
            }
            else {
                // the defender wins when their dice result was greater
                // or when there was a draw
                result.addAttackLoss();
            }

        }

        return result;
    }
}
