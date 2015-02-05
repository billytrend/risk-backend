package GameEngine;

import GameUtils.Results.FightResult;
import GameState.State;
import GameUtils.PlayerUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * The class holds the implementation of any random events
 * such as first player selection or dice throws.
 * 
 * It is also responsible for carrying out the fights. 
 *  ----- should it be?
 * 
 */
public class Arbitration{
	
    /**
     * Randomly selects a first player to start the game
     * @param state
     */
    public static void setFirstPlayer(State state) {
        Integer noOfPlayers = PlayerUtils.countPlayers(state);
        Random ran = new Random();
        Integer result = ran.nextInt(noOfPlayers);
        state.getPlayerQueue().setFirstPlayer(result);
    }
   
	/**
	 * Returns a random result of one die throw.
     * @return
     */
    private static Integer dieThrow(){
        Random ran = new Random();
        Integer result = ran.nextInt(6) + 1;
        return result;
    }

    /**
     * Returns a random result of multiple dice throw.
     *
     * @param numOfDice
     * @return
     */
    private static Integer[] nDiceThrow(int numOfDice){
        Integer[] result = new Integer[numOfDice];
        for(int i = 0; i < numOfDice; i++){
            result[i] = dieThrow();
        }
        return result;
    }

    
    /**
     * This method carries out the whole fight including dice throw,
     * and the analysis of its result. It returns the FightResult instance 
     * which represents the amount of units that should be taken of both
     * the defender and the attacker. 
     * 
     * @param result
     * @param dA
     * @param dB
     * @return
     */
    public static FightResult carryOutFight(FightResult result, int dA, int dB) {

        Integer[] attackDice = nDiceThrow(dA);
        Integer[] defendDice = nDiceThrow(dB);

        return arbitrateFight(result, attackDice, defendDice);
    }

    
    /**
     * The method takes the result of dice throws and compares them
     * to specify how many armies should be taken of each player.
     * The returned FightResult instance contains an information
     * about the amount of armies taken of the attacker and the defender.
     *
     * @param attacker
     * @param defender
     * @return
     */
    private static FightResult arbitrateFight(FightResult result, Integer[] attacker, Integer[] defender){

        // sort in descending order - to later compare the highest results
        Arrays.sort(attacker, Collections.reverseOrder());
        Arrays.sort(defender, Collections.reverseOrder());

        result.setAttackDiceRolled(attacker);
        result.setDefendDiceRolled(defender);

        // the amount compared is the smaller number of dice
        // that were thrown
        int numberToCompare = (attacker.length > defender.length) ? defender.length : attacker.length;

        for(int i = 0; i < numberToCompare; i++){
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
