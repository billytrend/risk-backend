package GameUtils;


import GameBuilders.RiskMapGameBuilder;
import GameState.*;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import com.esotericsoftware.minlog.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class CardUtilsTest {

    private State state;
    
    @Before
    public void setUp() throws Exception {
    	PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
    			new DumbBotInterface(), new DumbBotInterface()};

        state = RiskMapGameBuilder.buildGame(interfaces);
        Log.NONE();
    }
    @Test
    public void testAddDeck(){
    	assertEquals(state.getCards().size(), 44);
   
    }

    
//    @Test
//    public void testAddCard() throws Exception {
//        assertEquals(state.getCards().size(), 0);
//        Iterator<Territory> it = state.getTerritories().vertexSet().iterator();
//        Territory aTerr = it.next();
//        CardUtils.addCard(state, new Card(aTerr, CardType.CANNON));
//        assertEquals(state.getCards().size(), 1);
//        aTerr = it.next();
//        CardUtils.addCard(state, new Card(aTerr, CardType.HORSE));
//        assertEquals(state.getCards().size(), 2);
//        aTerr = it.next();
//        CardUtils.addCard(state, new Card(aTerr, CardType.SOLDIER));
//        assertEquals(state.getCards().size(), 3);
//    }

    @Test
    public void testGetPlayersCards() throws Exception {

        Iterator<Territory> it = state.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = state.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(state, c);

        assertEquals(CardUtils.getPlayersCards(state, aPlayer).size(), 0);
        CardUtils.givePlayerCard(c, aPlayer);
        assertEquals(CardUtils.getPlayersCards(state, aPlayer).size(), 1);
        assertEquals(CardUtils.getPlayersCards(state, aPlayer).get(0), c);
    }

    @Test
    public void testReleaseCard() throws Exception {
        Iterator<Territory> it = state.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = state.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(state, c);
        CardUtils.givePlayerCard(c, aPlayer);
        CardUtils.releaseCard(c);
        assertEquals(CardUtils.getPlayersCards(state, aPlayer).size(), 0);
    }

    @Test
    public void testGetUnownedCards() throws Exception {
    	assertEquals(44, CardUtils.getUnownedCards(state).size());
        Iterator<Territory> it = state.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = state.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(state, c);
        assertEquals(CardUtils.getUnownedCards(state).get(44), c);
        assertEquals(CardUtils.getUnownedCards(state).size(), 45);
        CardUtils.givePlayerCard(c, aPlayer);
        assertEquals(CardUtils.getUnownedCards(state).size(), 44);
        Card d = new Card(aTerr, CardType.HORSE);
        CardUtils.addCard(state, d);
        assertEquals(CardUtils.getUnownedCards(state).size(), 45);
        assertEquals(CardUtils.getUnownedCards(state).get(44), d);
    }

    @Test
    public void testGivePlayerRandomCard() throws Exception {
        Player aPlayer = state.getPlayers().get(0);
        CardUtils.givePlayerRandomCard(state, aPlayer);
        assertEquals(CardUtils.getPlayersCards(state, aPlayer).size(), 1);
        Player anotherPlayer = state.getPlayers().get(1);
        CardUtils.givePlayerRandomCard(state, anotherPlayer);
        assertNotEquals(
                CardUtils.getPlayersCards(state, aPlayer).get(0),
                CardUtils.getPlayersCards(state, anotherPlayer).get(0)
        );
    }

    @Test
    public void testGetCardsOfType() throws Exception {
      
      assertEquals(14, CardUtils.getCardsOfType(state, CardType.SOLDIER).size());
        assertEquals(14, CardUtils.getCardsOfType(state, CardType.CANNON).size());
        assertEquals(14, CardUtils.getCardsOfType(state, CardType.HORSE).size());
        assertEquals(2, CardUtils.getCardsOfType(state, CardType.WILD).size());
    }
    
    @Test
    public void testPayout(){
    	Player player = state.getPlayers().get(0);
    	assertEquals(0,player.getNumberOfCardSetsUsed());
    	assertEquals(4, CardUtils.getCurrentArmyPayout(player));
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	assertEquals(1,player.getNumberOfCardSetsUsed());
    	assertEquals(6, CardUtils.getCurrentArmyPayout(player));
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	assertEquals(3,player.getNumberOfCardSetsUsed());
    	assertEquals(10, CardUtils.getCurrentArmyPayout(player));
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	assertEquals(5,player.getNumberOfCardSetsUsed());
    	assertEquals(15, CardUtils.getCurrentArmyPayout(player));
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	CardUtils.incrementNumberOfCardSetsUsed(player);
    	assertEquals(7,player.getNumberOfCardSetsUsed());
    	assertEquals(25, CardUtils.getCurrentArmyPayout(player));
    	
    	Player newPlayer = state.getPlayers().get(1);
    	assertEquals(0,newPlayer.getNumberOfCardSetsUsed());
    	assertEquals(4, CardUtils.getCurrentArmyPayout(newPlayer));
    }

    @Test
    public void testGetPossibleCardCombinations() throws Exception {
    	Iterator<Territory> it = state.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Territory anotherTerr = it.next();
        Player aPlayer = state.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        Card d = new Card(aTerr, CardType.HORSE);
        Card e = new Card(aTerr, CardType.SOLDIER);
        Card f = new Card(anotherTerr, CardType.SOLDIER);
        Card g = new Card(anotherTerr, CardType.SOLDIER);
        Card h = new Card(anotherTerr, CardType.SOLDIER);
        assertEquals(0, CardUtils.getPossibleCardCombinations(state, aPlayer).size());
        CardUtils.addCard(state, c);
        CardUtils.addCard(state, d);
        CardUtils.addCard(state, e);
        CardUtils.givePlayerCard(c, aPlayer);
        CardUtils.givePlayerCard(d, aPlayer);
        CardUtils.givePlayerCard(e, aPlayer);
        assertEquals(1, CardUtils.getPossibleCardCombinations(state, aPlayer).size());
        assertTrue(CardUtils.getPossibleCardCombinations(state, aPlayer).get(0).contains(c));
        assertTrue(CardUtils.getPossibleCardCombinations(state, aPlayer).get(0).contains(d));
        assertTrue(CardUtils.getPossibleCardCombinations(state, aPlayer).get(0).contains(e));
        CardUtils.addCard(state, f);
        CardUtils.addCard(state, g);
        CardUtils.addCard(state, h);
        CardUtils.givePlayerCard(f, aPlayer);
        CardUtils.givePlayerCard(g, aPlayer);
        CardUtils.givePlayerCard(h, aPlayer);
       assertEquals(8, CardUtils.getPossibleCardCombinations(state, aPlayer).size());
    }
}