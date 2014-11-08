import java.util.ArrayList;


public class Player {

	// armies they have on board -- maybe for Ai?

	// armies to place on board
	int armiesToPlace;
	String id;
	// think about removing that? ??? 
	ArrayList<Territory> territories;

	ArrayList<Card> cards;
	
	Player(int armiesToPlace, String id){
		this.armiesToPlace = armiesToPlace;
		territories = new ArrayList<Territory>();
		cards = new ArrayList<Card>();
		this.id = id;
	}
	
}
