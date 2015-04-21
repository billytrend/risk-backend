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
public class Arbitration extends ArbitrationAbstract{
	
    /**
     * Randomly selects a first player to start the game
     * @param state
     */
    public void setFirstPlayer(State state) {
        Integer noOfPlayers = PlayerUtils.countPlayers(state);
        Random ran = new Random();
        Integer result = ran.nextInt(noOfPlayers);
        state.getPlayerQueue().setFirstPlayer(result);
    }
   
	/**
	 * Returns a random result of one die throw.
     * @return
     */
    public Integer dieThrow(){
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
    public Integer[] nDiceThrow(int numOfDice){
        Integer[] result = new Integer[numOfDice];
        for(int i = 0; i < numOfDice; i++){
            result[i] = dieThrow();
        }
        return result;
    }

	@Override
	public Integer dieThrowWrapper() {
		return dieThrow();
	}

}
