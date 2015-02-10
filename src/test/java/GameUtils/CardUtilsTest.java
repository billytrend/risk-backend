package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

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

    }

    @Test
    public void testGetUnownedCards() throws Exception {

    }

    @Test
    public void testGivePlayerRandomCard() throws Exception {

    }

    @Test
    public void testGetCardsOfType() throws Exception {

    }

    @Test
    public void testGetPossibleCardCombinations() throws Exception {

    }
}