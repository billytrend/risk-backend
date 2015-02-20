package GameState;

import GameUtils.ArmyUtils;
import PlayerInput.PlayerInterface;

import java.util.ArrayList;

/**
 * A class that represents a single player. Each player has a specific way of
 * communicating with the game engine, and a unique id. It also stores the
 * information about the number of armies that they still can deploy.
 * 
 */
public class Player {

	private String id = "Player " + this.hashCode();
	private PlayerInterface communicationMethod;
	private ArrayList<Army> armies;

	public Player(PlayerInterface communicationMethod, int startingArmies) {
		this.communicationMethod = communicationMethod;
		armies = new ArrayList<Army>();
		ArmyUtils.givePlayerNArmies(this, startingArmies);
	}

	public Player(PlayerInterface communicationMethod, int startingArmies,
			int index) {
		this.communicationMethod = communicationMethod;
		armies = new ArrayList<Army>();
		ArmyUtils.givePlayerNArmies(this, startingArmies);
		id = "Player " + index;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PlayerInterface getCommunicationMethod() {
		return communicationMethod;
	}

	public void setCommunicationMethod(PlayerInterface communicationMethod) {
		this.communicationMethod = communicationMethod;
	}

	public void setArmies(ArrayList<Army> armies) {
		this.armies = armies;
	}

	public ArrayList<Army> getArmies() {
		return armies;
	}
}
