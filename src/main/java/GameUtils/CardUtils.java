package GameUtils;

import GameState.Card;
import GameState.Player;
import GameState.State;

import java.util.ArrayList;
import java.util.Random;

public class CardUtils {
    
    public void addCard(State s, Card c) {
        s.getCards().add(c);
    }
    
    public ArrayList<Card> getPlayersCards(State s, Player p) {
        ArrayList<Card> cards = new ArrayList<Card>();
        
        for (Card c : s.getCards()) {
            if (c.getOwner().equals(p)) {
                cards.add(c);
            }
        }
        
        return cards;
    }
    
    public void givePlayerCard(Card c, Player p) {
        c.setOwner(p);
    }
    
    public void releaseCard(Card c) {
        c.setOwner(null);
    }
    
    public ArrayList<Card> getUnownedCards(State s) {
        ArrayList<Card> unownedCards = new ArrayList<Card>();
        for (Card c : s.getCards()) {
            if (c.getOwner().equals(null)) {
                unownedCards.add(c);
            }
        }
        return unownedCards;
    }
    
    public Card givePlayerRandomCard(State s, Player p) {
        ArrayList<Card> unownedCards = getUnownedCards(s);
        Random r = new Random();
        int randIndex = r.nextInt(unownedCards.size());
        Card chosen = unownedCards.get(randIndex);
        givePlayerCard(chosen, p);
        return chosen;
    }

}
