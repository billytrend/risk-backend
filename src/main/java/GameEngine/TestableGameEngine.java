package GameEngine;

import GameState.Army;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.Results.FightResult;
import GameUtils.PlayerUtils;
import GameUtils.RuleUtils;
import GameUtils.TerritoryUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static GameEngine.PlayState.*;
import static com.esotericsoftware.minlog.Log.debug;

/**
 * An instance of this class represents a game that
 * is currently being played. Several games can be played
 * at a time.
 * 
 */
public class TestableGameEngine extends GameEngine {

	public TestableGameEngine(State state, WinConditions conditions) {
		super(state, conditions);
	}
	
	public TestableGameEngine(State state) {
		super(state);
	}

	// for testing purposes
	public void setFirstPlayer(int index){
	    gameState.getPlayerQueue().setFirstPlayer(index);
		currentPlayer = gameState.getPlayerQueue().getCurrent();
	}

	public void nextPlayer(){
		currentPlayer = gameState.getPlayerQueue().next();
	}

	public Player getCurrentPlayer(){
		return currentPlayer;
	}
	
	public PlayState testCall(PlayState callType){
		switch(callType){
			case FILLING_EMPTY_COUNTRIES:
				return super.fillAnEmptyCountry();
			case USING_REMAINING_ARMIES:
				return useARemainingArmy();
			case PLAYER_PLACING_ARMIES:
				return placeArmy();
			case PLAYER_INVADING_COUNTRY:
				return invadeCountry();
			case PLAYER_MOVING_ARMIES:
				return moveArmy();
			default:
				return null;
		}
	}
}