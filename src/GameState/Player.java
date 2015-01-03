package GameState;

import GameState.StateUtils.ArmyUtils;
import PlayerInput.PlayerInterface;

import java.util.ArrayList;


public class Player {

	private String id;
	PlayerInterface interfaceMethod;
	private ArrayList<Army> armies;

	public Player(String id, PlayerInterface interfaceMethod, int startingArmies) {
		this.id = id;
		this.interfaceMethod = interfaceMethod;
		armies = new ArrayList<Army>();
		ArmyUtils.givePlayerNArmies(this, startingArmies);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PlayerInterface getInterfaceMethod() {
		return interfaceMethod;
	}

	public void setInterfaceMethod(PlayerInterface interfaceMethod) {
		this.interfaceMethod = interfaceMethod;
	}

	public void setArmies(ArrayList<Army> armies) {
		this.armies = armies;
	}

	public ArrayList<Army> getArmies() {
		return armies;
	}
}
