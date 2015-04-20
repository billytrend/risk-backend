package GameUtils.Results;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import GameUtils.AIUtils;
import GameUtils.ArmyUtils;
import GameUtils.TerritoryUtils;
import GameUtils.Results.Change;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class StateChangeRecordTest {

	private StateChangeRecord changeRecord;
	private ArrayList<Player>  players;
	private State gameState;
	@Before
	public void setUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
        gameState = DemoGameBuilder.buildGame(interfaces);
        GameEngine gameThr = new GameEngine(gameState);
        changeRecord = gameThr.getStateChangeRecord();
        gameThr.run();
        players = gameState.getPlayers();
       
	}
	
    @Test
    public void recordSizeTest() {
   	
        assertTrue(changeRecord.getSize() > 0);
        assertTrue(changeRecord.getPlayersTurns().size() > 0);
        assertTrue(changeRecord.getPlayersTurns().size() < changeRecord.getSize());
    }
    
    @Test
    public void getPlayersTurnsTest(){
    	ArrayList<ArrayList<Change>> playersTurns = changeRecord.getPlayersTurns();
    	String player;
    	for(ArrayList<Change> turnChanges : playersTurns){
    		if(turnChanges.size() <= 0)
    			fail();
    		player = turnChanges.get(0).getActingPlayerId();
    		for(Change change : turnChanges){
    			assertEquals(change.getActingPlayerId(), player);
    		}
    	}
    }
    
    
    @Test
    public void stateChangesTest(){
    	 Territory[] territories = new Territory[TerritoryUtils.getAllTerritories(gameState).size()];
    	 
    	ArrayList<String> playerIds = new ArrayList<String>();
    	for(Player p : players){
    		playerIds.add(p.getId());
    		ArmyUtils.givePlayerNArmies(p, 20);
    	}
    	String player1 = playerIds.get(0);
    	String player2 = playerIds.get(1);
    	HashSet<Territory> territorySet = TerritoryUtils.getAllTerritories(gameState);
       	ArrayList<String> territoryIds = new ArrayList<String>();
    	for(Territory t : territorySet) territoryIds.add(t.getId());
    	Territory territory = AIUtils.getRandomTerritory(gameState, territorySet);
    	Territory territory2 = AIUtils.getRandomTerritory(gameState, territorySet
    			);
    	ArmyPlacement change1 = new ArmyPlacement(player1,territory.getId(), 1, PlayState.PLAYER_MOVING_ARMIES);
    	ArmyMovement change2 = new ArmyMovement(player1,territory.getId(), territory2.getId(),1, PlayState.PLAYER_INVADING_COUNTRY);
    	PlayerRemoval change3 = new PlayerRemoval(player1, player2);
    	
    	StateChangeRecord record = new StateChangeRecord(playerIds, territoryIds, 30 );
    	record.addStateChange(change1);
    	record.addStateChange(change2);
    	record.addStateChange(change3);
    	
    	record.applyAllChanges(gameState);
    	assertEquals(change3, record.getLastChange());
    	ArrayList<Change> changes = record.getNLastChanges(2);
    	assertEquals(2, changes.size());
    	assertEquals(change2, changes.get(0));
    	assertEquals(change3, changes.get(1));
    	
    	changes = record.getNLastChanges(10);
    	assertEquals(3, changes.size());
    	assertTrue(changes.contains(change1));
    	assertTrue(changes.contains(change2));
    	assertTrue(changes.contains(change3));
    	
    }
   
    
}
