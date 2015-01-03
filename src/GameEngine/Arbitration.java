package GameEngine;

import GameState.Events.FightResult;
import GameState.State;
import GameState.StateUtils.StateStats;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by bt on 30/12/2014.
 */
public class Arbitration {

    private static int dieThrow(){
        Random ran = new Random();
        int result = ran.nextInt(6) + 1;
        return result;
    }

    private static int[] nDiceThrow(int numOfDice){
        int[] result = new int[numOfDice];
        for(int i = 0; i < numOfDice; i++){
            result[i] = dieThrow();
        }
        return result;
    }

    public static void setFirstPlayer(State state) {
        int noOfPlayers = StateStats.countPlayers(state);
        Random ran = new Random();
        int result = ran.nextInt(noOfPlayers);
        state.getPlayerQueue().setFirstPlayer(result);
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
    private static FightResult arbitrateFight(int[] attacker, int[] defender){
        Arrays.sort(attacker);
        Arrays.sort(defender);

        FightResult result = new FightResult();
        result.setAttackDiceRolled(attacker);
        result.setDefendDiceRolled(defender);

        // the amount compared is the smaller number of dice
        // that were thrown
        int numOfCompared = (attacker.length > defender.length)
                ? defender.length : attacker.length;

        for(int i = 1; i <= numOfCompared; i++){
            if(attacker[attacker.length - i] > defender[defender.length - i]) {
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
