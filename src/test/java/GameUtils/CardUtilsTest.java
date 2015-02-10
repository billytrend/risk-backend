package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.Card;
import GameState.CardType;
import GameState.State;
import GameState.Territory;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class CardUtilsTest {

    private State st;
    
    @Before
    public void setUp() throws Exception {
        this.st = DemoGameBuilder.buildGame(4, 4);
    }

    @Test
    public void testAddCard() throws Exception {
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

    }

    @Test
    public void testGivePlayerCard() throws Exception {

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