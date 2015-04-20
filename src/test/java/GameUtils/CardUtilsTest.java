package GameUtils;

import GameBuilders.RiskMapGameBuilder;
import GameState.*;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import com.esotericsoftware.minlog.Log;

import org.javatuples.Triplet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.*;

public class CardUtilsTest {

	private State state;

	@Before
	public void setUp() throws Exception {
		PlayerInterface[] interfaces = new PlayerInterface[] {
				new DumbBotInterface(), new DumbBotInterface(),
				new DumbBotInterface(), new DumbBotInterface() };

		state = RiskMapGameBuilder.buildGame(interfaces);
		Log.NONE();
	}

	@Test
	public void testAddDeck() {
		assertEquals(state.getCards().size(), 44);

	}

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
	public void testReleaseCards() {
		Player aPlayer = state.getPlayers().get(0);
		ArrayList<Card> cards = CardUtils.getUnownedCards(state);
		for (int i = 0; i < 5; i++) {
			CardUtils.givePlayerCard(cards.get(i), aPlayer);
		}
		ArrayList<Card> playersCards = CardUtils.getPlayersCards(state, aPlayer);
		Card a = playersCards.get(0);
		Card b = playersCards.get(1);
		Card c = playersCards.get(2);
		
		Triplet<Card, Card, Card> trio = new Triplet<Card, Card, Card>(a, b, c);

		assertEquals(5, CardUtils.getPlayersCards(state, aPlayer).size());
		CardUtils.releaseCards(trio);
		assertEquals(2, CardUtils.getPlayersCards(state, aPlayer).size());

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
		assertNotEquals(CardUtils.getPlayersCards(state, aPlayer).get(0),
				CardUtils.getPlayersCards(state, anotherPlayer).get(0));
		
		for(Card c: CardUtils.getUnownedCards(state)){
			CardUtils.givePlayerCard(c, aPlayer);
		}
		assertEquals(0, CardUtils.getUnownedCards(state).size());
		int numPlayersCards = CardUtils.getPlayersCards(state, aPlayer).size();
		CardUtils.givePlayerRandomCard(state, aPlayer);
		assertEquals(numPlayersCards, CardUtils.getPlayersCards(state, aPlayer).size());
	}

	@Test
	public void testGetCardsOfType() throws Exception {

		assertEquals(14, CardUtils.getCardsOfType(state, CardType.SOLDIER)
				.size());
		assertEquals(14, CardUtils.getCardsOfType(state, CardType.CANNON)
				.size());
		assertEquals(14, CardUtils.getCardsOfType(state, CardType.HORSE).size());
		assertEquals(2, CardUtils.getCardsOfType(state, CardType.WILD).size());
	}

	@Test
	public void testPayout() {
		ArrayList<Card> cards = CardUtils.getUnownedCards(state);
		Triplet<Card,Card,Card> cardSet = new Triplet<Card, Card, Card>(cards.get(0), cards.get(1), cards.get(2));
		Player player = state.getPlayers().get(0);
		assertEquals(0, player.getNumberOfCardSetsUsed());
		assertEquals(4, CardUtils.getCurrentArmyPayout(player, cardSet));
		CardUtils.incrementNumberOfCardSetsUsed(player);
		assertEquals(1, player.getNumberOfCardSetsUsed());
		assertEquals(6, CardUtils.getCurrentArmyPayout(player, cardSet));
		CardUtils.incrementNumberOfCardSetsUsed(player);
		CardUtils.incrementNumberOfCardSetsUsed(player);
		assertEquals(3, player.getNumberOfCardSetsUsed());
		assertEquals(10, CardUtils.getCurrentArmyPayout(player, cardSet));
		CardUtils.incrementNumberOfCardSetsUsed(player);
		CardUtils.incrementNumberOfCardSetsUsed(player);
		assertEquals(5, player.getNumberOfCardSetsUsed());
		assertEquals(15, CardUtils.getCurrentArmyPayout(player, cardSet));
		CardUtils.incrementNumberOfCardSetsUsed(player);
		CardUtils.incrementNumberOfCardSetsUsed(player);
		assertEquals(7, player.getNumberOfCardSetsUsed());
		assertEquals(25, CardUtils.getCurrentArmyPayout(player, cardSet));

		Player newPlayer = state.getPlayers().get(1);
		assertEquals(0, newPlayer.getNumberOfCardSetsUsed());
		assertEquals(4, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
		
		ArmyUtils.givePlayerNArmies(newPlayer, 3);
		ArmyUtils.deployArmies(newPlayer, cardSet.getValue0().getTerritory(), 1);
		assertEquals(6, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
		ArmyUtils.deployArmies(newPlayer, cardSet.getValue1().getTerritory(), 1);
		assertEquals(6, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
		ArmyUtils.deployArmies(newPlayer, cardSet.getValue2().getTerritory(), 1);
		assertEquals(6, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
		ArmyUtils.destroyArmies(newPlayer, cardSet.getValue0().getTerritory(), 1);
		assertEquals(6, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
		ArmyUtils.destroyArmies(newPlayer, cardSet.getValue1().getTerritory(), 1);
		assertEquals(6, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
		ArmyUtils.destroyArmies(newPlayer, cardSet.getValue2().getTerritory(), 1);
		assertEquals(4, CardUtils.getCurrentArmyPayout(newPlayer, cardSet));
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
		assertEquals(0, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
		CardUtils.addCard(state, c);
		CardUtils.addCard(state, d);
		CardUtils.addCard(state, e);
		CardUtils.givePlayerCard(c, aPlayer);
		CardUtils.givePlayerCard(d, aPlayer);
		CardUtils.givePlayerCard(e, aPlayer);
		assertEquals(1, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
		assertTrue(CardUtils.getPossibleCardCombinations(state, aPlayer).get(0)
				.contains(c));
		assertTrue(CardUtils.getPossibleCardCombinations(state, aPlayer).get(0)
				.contains(d));
		assertTrue(CardUtils.getPossibleCardCombinations(state, aPlayer).get(0)
				.contains(e));
		CardUtils.addCard(state, f);
		CardUtils.addCard(state, g);
		CardUtils.addCard(state, h);
		CardUtils.givePlayerCard(f, aPlayer);
		CardUtils.givePlayerCard(g, aPlayer);
		CardUtils.givePlayerCard(h, aPlayer);
		assertEquals(8, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
	}
	@Test
	public void threeOfaKindTest(){
		Iterator<Territory> it = state.getTerritories().vertexSet().iterator();
		Player aPlayer = state.getPlayers().get(0);
		assertEquals(0, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
		assertEquals(0, CardUtils.getPlayersCards(state, aPlayer)
				.size());
		Card a = new Card(it.next(), CardType.HORSE);
		Card b = new Card(it.next(), CardType.HORSE);
		Card c = new Card(it.next(), CardType.HORSE);
		CardUtils.addCard(state, a);
		CardUtils.addCard(state, b);
		CardUtils.addCard(state, c);
		
		CardUtils.givePlayerCard(a, aPlayer);
		CardUtils.givePlayerCard(b, aPlayer);
		CardUtils.givePlayerCard(c, aPlayer);
		assertEquals(1, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
		Triplet<Card, Card, Card> trio = new Triplet<Card, Card, Card>(a,b,c);
		CardUtils.releaseCards(trio);
		assertEquals(0, CardUtils.getPlayersCards(state, aPlayer)
				.size());
		Card d = new Card(it.next(), CardType.SOLDIER);
		Card e = new Card(it.next(), CardType.SOLDIER);
		Card f = new Card(it.next(), CardType.SOLDIER);
		CardUtils.addCard(state, d);
		CardUtils.addCard(state, e);
		CardUtils.addCard(state, f);
		
		CardUtils.givePlayerCard(d, aPlayer);
		CardUtils.givePlayerCard(e, aPlayer);
		CardUtils.givePlayerCard(f, aPlayer);
		assertEquals(1, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
		
		Card g = new Card(it.next(), CardType.CANNON);
		Card h = new Card(it.next(), CardType.CANNON);
		Card i = new Card(it.next(), CardType.CANNON);
		CardUtils.addCard(state, g);
		CardUtils.addCard(state, h);
		CardUtils.addCard(state, i);
		
		CardUtils.givePlayerCard(g, aPlayer);
		CardUtils.givePlayerCard(h, aPlayer);
		CardUtils.givePlayerCard(i, aPlayer);
		assertEquals(2, CardUtils.getPossibleCardCombinations(state, aPlayer)
				.size());
	}
	
}