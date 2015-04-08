package GameUtils;

import GameState.Card;
import GameState.CardType;
import GameState.Player;
import GameState.State;

import org.javatuples.Triplet;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.Random;

public class CardUtils {

    public static int getCurrentArmyPayout(Player player) {
        int payout = 4;
        int setsUsed = player.getNumberOfCardSetsUsed();
        //first 5 sets, add 2 to payout
        for(int i = 0; i < setsUsed && i < 4 ;i++) payout +=2;
        //sixth set add 3
        if(setsUsed >=5) payout +=3;
        //add another 5 for each set after the sixth
        for(int i = 5; i < setsUsed; i++) payout +=5;
        
        return payout;
        
    }
    
    public static void incrementNumberOfCardSetsUsed(Player player) {
        player.incrementNumberOfCardSetsUsed();
    }

    public static void addCard(State s, Card c) {
        s.getCards().add(c);
    }
    
    public static ArrayList<Card> getPlayersCards(State s, Player p) {
        ArrayList<Card> cards = new ArrayList<Card>();
        
        for (Card c : s.getCards()) {
            if (c.getOwner() != null && c.getOwner().equals(p)) {
                cards.add(c);
            }
        }
        
        return cards;
    }
    
    public static void givePlayerCard(Card c, Player p) {
    	Log.debug("give player card");
        c.setOwner(p);
    }
    
    public static void releaseCard(Card c) {
        c.setOwner(null);
    }
    
    public static void releaseCards(Triplet<Card, Card, Card> cards) {
        for (Object c : cards) {
            releaseCard((Card) c);
        }
    }
    
    public static ArrayList<Card> getUnownedCards(State s) {
        ArrayList<Card> unownedCards = new ArrayList<Card>();
        for (Card c : s.getCards()) {
            if (c.getOwner() == null) {
                unownedCards.add(c);
            }
        }
        return unownedCards;
    }
    
    public static void givePlayerRandomCard(State s, Player p) {
        ArrayList<Card> unownedCards = getUnownedCards(s);
        if (unownedCards.size() == 0) return;
        Random r = new Random();
        int randIndex = r.nextInt(unownedCards.size());
        Card chosen = unownedCards.get(randIndex);
        givePlayerCard(chosen, p);
    }
    
    public static ArrayList<Card> getCardsOfType(ArrayList<Card> cards, CardType cardType) {
        ArrayList<Card> cardsOfType = new ArrayList<Card>();
        for (Card c : cards) {
            if (c.getType() == cardType) {
                cardsOfType.add(c);
            }
        }
        return cardsOfType;
    }
    public static ArrayList<Card> getCardsOfType(State state, CardType cardType) {
        return getCardsOfType(state.getCards(), cardType);
    }

    public static ArrayList<Triplet<Card, Card, Card>> getPossibleCardCombinations(State s, Player p) {
        ArrayList<Card> playersCards = getPlayersCards(s, p);
        ArrayList<Triplet<Card, Card, Card>> combinations = new ArrayList<Triplet<Card, Card, Card>>();

        ArrayList<Card> horseCards = getCardsOfType(playersCards, CardType.HORSE);
        ArrayList<Card> soldierCards = getCardsOfType(playersCards, CardType.SOLDIER);
        ArrayList<Card> cannonCards = getCardsOfType(playersCards, CardType.CANNON);
        
        for (Card horseCard : horseCards) {
            for (Card soldierCard : soldierCards) {
                for (Card cannonCard : cannonCards) {
                    combinations.add(new Triplet<Card, Card, Card>(horseCard, soldierCard, cannonCard));
                }
            }
        }

        if (horseCards.size() > 2) {
            ICombinatoricsVector<Card> initialVector = Factory.createVector(horseCards);
            Generator<Card> gen = Factory.createSimpleCombinationGenerator(initialVector, 3);
            for (ICombinatoricsVector<Card> combination : gen) {
                Triplet<Card, Card, Card> trip = new Triplet<Card, Card, Card>(
                        combination.getValue(0),
                        combination.getValue(1),
                        combination.getValue(2)
                );
                combinations.add(trip);
            }
        }
        
        if (soldierCards.size() > 2) {
            ICombinatoricsVector<Card> initialVector = Factory.createVector(soldierCards);
            Generator<Card> gen = Factory.createSimpleCombinationGenerator(initialVector, 3);
            for (ICombinatoricsVector<Card> combination : gen) {
                Triplet<Card, Card, Card> trip = new Triplet<Card, Card, Card>(
                        combination.getValue(0),
                        combination.getValue(1),
                        combination.getValue(2)
                );
                combinations.add(trip);
            }
        }
        
        if (cannonCards.size() > 2) {
            ICombinatoricsVector<Card> initialVector = Factory.createVector(cannonCards);
            Generator<Card> gen = Factory.createSimpleCombinationGenerator(initialVector, 3);
            for (ICombinatoricsVector<Card> combination : gen) {
                Triplet<Card, Card  , Card> trip = new Triplet<Card, Card, Card>(
                        combination.getValue(0),
                        combination.getValue(1),
                        combination.getValue(2)
                );
                combinations.add(trip);
            }
        }
        return combinations;
    }

}
