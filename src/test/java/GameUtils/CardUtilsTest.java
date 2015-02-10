package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CardUtilsTest {

    private State st;
    
    @Before
    public void setUp() throws Exception {
        this.st = DemoGameBuilder.buildGame(4, 4);
    }

    @Test
    public void testAddCard() throws Exception {
        assertEquals(this.st.getCards().size(), 0);
        Iterator<Territory> it = this.st.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        CardUtils.addCard(this.st, new Card(aTerr, CardType.CANNON));
        assertEquals(this.st.getCards().size(), 1);
        aTerr = it.next();
        CardUtils.addCard(this.st, new Card(aTerr, CardType.HORSE));
        assertEquals(this.st.getCards().size(), 2);
        aTerr = it.next();
        CardUtils.addCard(this.st, new Card(aTerr, CardType.SOLDIER));
        assertEquals(this.st.getCards().size(), 3);
    }

    @Test
    public void testGetPlayersCards() throws Exception {

        Iterator<Territory> it = this.st.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = this.st.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(this.st, c);

        assertEquals(CardUtils.getPlayersCards(this.st, aPlayer).size(), 0);
        CardUtils.givePlayerCard(c, aPlayer);
        assertEquals(CardUtils.getPlayersCards(this.st, aPlayer).size(), 1);
        assertEquals(CardUtils.getPlayersCards(this.st, aPlayer).get(0), c);
    }

    @Test
    public void testReleaseCard() throws Exception {
        Iterator<Territory> it = this.st.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = this.st.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(this.st, c);
        CardUtils.givePlayerCard(c, aPlayer);
        CardUtils.releaseCard(c);
        assertEquals(CardUtils.getPlayersCards(this.st, aPlayer).size(), 0);
    }

    @Test
    public void testGetUnownedCards() throws Exception {
        Iterator<Territory> it = this.st.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = this.st.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(this.st, c);
        assertEquals(CardUtils.getUnownedCards(this.st).get(0), c);
        assertEquals(CardUtils.getUnownedCards(this.st).size(), 1);
        CardUtils.givePlayerCard(c, aPlayer);
        assertEquals(CardUtils.getUnownedCards(this.st).size(), 0);
        Card d = new Card(aTerr, CardType.HORSE);
        CardUtils.addCard(this.st, d);
        assertEquals(CardUtils.getUnownedCards(this.st).size(), 1);
        assertEquals(CardUtils.getUnownedCards(this.st).get(0), d);

    }

    @Test
    public void testGivePlayerRandomCard() throws Exception {
        Iterator<Territory> it = this.st.getTerritories().vertexSet().iterator();
        Territory aTerr = it.next();
        Player aPlayer = this.st.getPlayers().get(0);
        Card c = new Card(aTerr, CardType.CANNON);
        CardUtils.addCard(this.st, c);
        Card d = new Card(aTerr, CardType.HORSE);
        CardUtils.addCard(this.st, d);
        CardUtils.givePlayerRandomCard(this.st, aPlayer);
        assertTrue(
                CardUtils.getPlayersCards(this.st, aPlayer).get(0).equals(c) ||
                CardUtils.getPlayersCards(this.st, aPlayer).get(0).equals(d)
        );

        Player anotherPlayer = this.st.getPlayers().get(1);
        CardUtils.givePlayerRandomCard(this.st, anotherPlayer);
        assertNotEquals(
                CardUtils.getPlayersCards(this.st, aPlayer).get(0),
                CardUtils.getPlayersCards(this.st, anotherPlayer).get(0)
        );
    }

    @Test
    public void testGetCardsOfType() throws Exception {

    }

    @Test
    public void testGetPossibleCardCombinations() throws Exception {

    }
}