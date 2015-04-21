package GameEngine;

import java.util.ArrayList;

import GameState.State;

public class NetworkArbitration extends ArbitrationAbstract{

	private int firstPlayerId = 0;
	private ArrayList<Integer> diceThrowResult = null;
	private final int[] rolls = new int[]{4, 2, 2, 5, 3, 6, 6, 1, 2, 3, 5, 6, 2, 4, 4};
	private int lastIndexUsed = 0;
	
	@Override
	public void setFirstPlayer(State state) {
        state.getPlayerQueue().setFirstPlayer(firstPlayerId);
	}

	@Override
	public Integer[] nDiceThrow(int numOfDice) {
		Integer[] result = new Integer[numOfDice];
	/*	if(diceThrowResult.size() != numOfDice){
			return null;
		}
		for(int i = 0; i < numOfDice; i++){
			result[i] = diceThrowResult.get(i);
		}*/
		int index;
		for(int i = 0; i < numOfDice; i++){
			index = ((lastIndexUsed + i) % rolls.length);
			result[i] = rolls[index];
		}
		lastIndexUsed += numOfDice;
		
		return result;
	}
	
	
	

	public void setDiceThrowResult(ArrayList<Integer> diceThrowResult) {
		this.diceThrowResult = diceThrowResult;
	}
	
	public void setFirstPlayerId(int firstPlayerId) {
		this.firstPlayerId = firstPlayerId;
	}

	@Override
	public Integer dieThrowWrapper() {
		// TODO Auto-generated method stub
		return null;
	}

}
