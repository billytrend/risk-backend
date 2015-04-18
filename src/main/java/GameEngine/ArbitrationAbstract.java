package GameEngine;

import java.util.Arrays;
import java.util.Collections;

import GameState.State;
import GameUtils.Results.FightResult;

public abstract class ArbitrationAbstract {

	    public abstract void setFirstPlayer(State state);
	 
	    public abstract Integer dieThrowWrapper();
	     

	    /**
	     * Returns a random result of multiple dice throw.
	     *
	     * @param numOfDice
	     * @return
	     */
	    public abstract Integer[] nDiceThrow(int numOfDice);

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
	   public FightResult carryOutFight(FightResult result, int dA, int dB) {

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
	   private FightResult arbitrateFight(FightResult result, Integer[] attacker, Integer[] defender){

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
