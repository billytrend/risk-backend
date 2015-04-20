package GameState;

import PlayerInput.PlayerInterface;

import java.util.ArrayList;

/**
 * A class that represents a single player. Each player
 * has a specific way of communicating with the game engine,
 * and a unique id. It also stores the information about the
 * number of armies that they still can deploy.
 * 
 */
public class Player {

	private String id = "Player " + this.hashCode();
	private String publicKey = ""; 	
	private int numberId;
	private transient PlayerInterface communicationMethod;
	private transient ArrayList<Army> armies = new ArrayList<Army>();
    private int cardSetsUsed;
    private String colour = "";
    private int numberOfTurns;
    private int numberOfAttacks;

	public Player(PlayerInterface communicationMethod) {
		this.communicationMethod = communicationMethod;
		armies = new ArrayList<Army>();
		cardSetsUsed = 0;
		numberOfTurns = 0;
		numberOfAttacks =0;
	}

	public Player(PlayerInterface communicationMethod, int index) {
		this(communicationMethod);
		id = "Player " + index;
		numberId = index;
		numberOfTurns = 0;
		numberOfAttacks =0;
	}
	
	public Player(PlayerInterface communicationMethod, String id) {
		this(communicationMethod);
		this.id = id;
		numberOfTurns = 0;
		numberOfAttacks =0;
	}
	
	public Player(PlayerInterface communicationMethod, Integer id, String name) {
		this(communicationMethod);
		this.numberId = id;
		this.id = name;
		numberOfTurns = 0;
		numberOfAttacks =0;
	}

	public String getId() {
		return id;
	}
	
	public int getNumberId() {
		return numberId;
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

	public int getNumberOfCardSetsUsed() {
		return cardSetsUsed;
	}

	public void incrementNumberOfCardSetsUsed() {
		this.cardSetsUsed ++;
	}

    public String getColour() {
        return colour;
    }
    public void setColour(String colour) {
        this.colour = colour;
    }

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	public void logTurn(){
		numberOfTurns ++;
	}

    public void logAttack(){
		numberOfAttacks++;
	}

    public int getNumberOfTurns(){
		return numberOfTurns;
	}

    public int getNumberOfAttacks(){
		return numberOfAttacks;
	}

}
