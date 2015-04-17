package GameEngine;

import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;

import java.util.ArrayList;

public class WinConditions {
	private int numOfPlayersRemaining;
	
	private int numOfTerritoriesToWin;
	
	public WinConditions(int playersToRemain, int territoriesNumber){
		numOfPlayersRemaining = playersToRemain;
		numOfTerritoriesToWin = territoriesNumber;
	}
	

	public WinConditions() {
		numOfPlayersRemaining = 1;
		numOfTerritoriesToWin = 0;
	}


	public int getNumberOfTerritoriesToWin() {
		return numOfTerritoriesToWin;
	}
	public void setNumOfTerritoriesToWin(int numberOfTerritoriesToWin) {
		this.numOfTerritoriesToWin = numberOfTerritoriesToWin;
	}

	public int getNumOfPlayersRemaining() {
		return numOfPlayersRemaining;
	}

	public void setNumOfPlayersRemaining(int numberOfPlayersRemained) {
		this.numOfPlayersRemaining = numberOfPlayersRemained;
	}
	
	public boolean checkConditions(State state){
		if(numOfTerritoriesToWin != 0){
			ArrayList<Player> allPlayers = PlayerUtils.getPlayersInGame(state);
			for(Player player : allPlayers){
				if(TerritoryUtils.getPlayersTerritories(player).size() >= numOfTerritoriesToWin)
					return true;
			}
		}
		
		if(PlayerUtils.countPlayers(state) == numOfPlayersRemaining){
			return true;
		}
		
		return false;
	}
	
	
	
}
