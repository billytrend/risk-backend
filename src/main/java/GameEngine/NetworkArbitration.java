package GameEngine;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import GameState.State;
import GameUtils.Results.FightResult;

public class NetworkArbitration extends ArbitrationAbstract{

	private int firstPlayerId = 0;
	private ArrayList<Integer> diceThrowResult = null;
	private BlockingQueue<Integer> rolls = new LinkedBlockingDeque<Integer>();
	
	
	@Override
	public void setFirstPlayer(State state) {
		System.out.println("setting first player: " + firstPlayerId);
        state.getPlayerQueue().setFirstPlayer(firstPlayerId);
	}
	
	
	@Override
    public FightResult carryOutFight(FightResult result, int dA, int dB) {

		 System.out.println("throwing " + dA + "  def: " + dB);
	     try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Integer[] attackDice = nDiceThrow(dA);
	     Integer[] defendDice = nDiceThrow(dB);

	     return super.arbitrateFight(result, attackDice, defendDice);
	 }

	@Override
	public Integer[] nDiceThrow(int numOfDice) {
		Integer[] result = new Integer[numOfDice];
		System.out.println("Roll results:");
		for(int i = 0; i < numOfDice; i++){
			try {
				result[i] = rolls.take();
				System.out.print(result[i] + "  ");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
		return result;
	}
	
	
	public void addDieThrowResult(Integer result) {
		rolls.add(result);
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
