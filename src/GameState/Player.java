import java.util.ArrayList;


public class Player {

	// armies they have on board -- maybe for Ai?

	// armies to place on board
	int armiesToPlace;
	
	// think about removing that? ??? 
	ArrayList<Territory> territories;

	ArrayList<Card> cards;
	
	Player(int armiesToPlace){
		this.armiesToPlace = armiesToPlace;
		territories = new ArrayList<Territory>();
		cards = new ArrayList<Card>();
	}
	
}
