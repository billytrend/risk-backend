package GameEngine;

import java.util.ArrayList;

import GameState.State;

public class NetworkArbitration extends ArbitrationAbstract{

	private int firstPlayerId = -1;
	private ArrayList<Integer> diceThrowResult = null;
	
	@Override
	public void setFirstPlayer(State state) {
        state.getPlayerQueue().setFirstPlayer(firstPlayerId);
	}

	@Override
	public Integer[] nDiceThrow(int numOfDice) {
		Integer[] result = new Integer[numOfDice];
		if(diceThrowResult.size() != numOfDice){
			return null;
		}
		for(int i = 0; i < numOfDice; i++){
			result[i] = diceThrowResult.get(i);
		}
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
