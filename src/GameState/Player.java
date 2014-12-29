package GameState;

import java.util.ArrayList;


public class Player {

	// armies they have on board -- maybe for Ai?

	// armies to place on board
	public int armiesToPlace;
	public String id;
	public ArrayList<Territory> territories;

	ArrayList<Card> cards;
	
	public Player(int armiesToPlace, String id){
		this.armiesToPlace = armiesToPlace;
		territories = new ArrayList<Territory>();
		cards = new ArrayList<Card>();
		this.id = id;
	}
	
}
