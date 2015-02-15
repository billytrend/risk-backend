package GameUtils.Results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import GameEngine.PlayState;
import GameState.Army;
import GameState.Player;
import GameState.Territory;

public class StateChange {
	
	private Player actingPlayer;
	private PlayState actionPlayed;
	private ArrayList<ArmyMovement> armyMovements;	
	private ArrayList<ArmyDeletion> destroyedArmies;
	private ArrayList<Player> removedPlayers;
	
	public StateChange(PlayState actionPlayed, Player actingPlayer){
		this.actingPlayer = actingPlayer;
		this.actionPlayed = actionPlayed;
		armyMovements = new ArrayList<ArmyMovement>();
		destroyedArmies = new ArrayList<ArmyDeletion>();
		removedPlayers = new ArrayList<Player>();
	}
	
	
	public void addArmyMovement(Player owner, Territory from, Territory to, int amount){
		armyMovements.add(new ArmyMovement(owner, from, to, amount));
	}
	
	public void addDestroyedArmies(Player player, Territory territory, int amount) {
		destroyedArmies.add(new ArmyDeletion(player, territory, amount));
	}

	public void addRemovedPlayer(Player player){
		removedPlayers.add(player);
	}
	
	// show change?
	public String toString(){
		String change = "PLAYER: " + actingPlayer.getId() + "  ACTION: " + actionPlayed.toString();
		if(armyMovements.size() > 0){
			change += "\n\n\tARMY MOVEMENTS:";
			for(ArmyMovement movement : armyMovements){
				change += "\n\t" + movement.toString();
			}
		}
		if(destroyedArmies.size() > 0){
			change += "\n\n\tARMIES DESTROYED:";
			for(ArmyDeletion deletion : destroyedArmies){
				change += "\n\t" + deletion.toString();
			}
		}
		if(removedPlayers.size() > 0){
			change += "\n\n\tPLAYERS REMOVED:";
			for(Player player : removedPlayers){
				change += "\n\t" + player.getId();
			}
		}
		return change;
	}
	
	public ArrayList<ArmyMovement> getArmyMovements(){
		return armyMovements;
	}
	
	public ArrayList<ArmyDeletion> getDestroyedArmies(){
		return destroyedArmies;
	}
	
	public Player getActingPlayer() {
		return actingPlayer;
	}
	public PlayState getActionPlayed() {
		return actionPlayed;
	}

	public ArrayList<Player> getRemovedPlayers() {
		return removedPlayers;
	}
	public void setRemovedPlayers(ArrayList<Player> removedPlayers) {
		this.removedPlayers = removedPlayers;
	}



	
	/*
	 
	 what can change?
	 
	 Army has moved to another territory
	 Army has been removed
	 Army had been created and placed
		-- this defines territory ownership
	 
	 Player can be removed from the queue of players
	 
	 */
}
