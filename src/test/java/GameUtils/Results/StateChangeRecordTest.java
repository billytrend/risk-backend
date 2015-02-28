package GameUtils.Results;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

public class StateChangeRecordTest {

	private StateChangeRecord changeRecord;

	@Before
	public void setUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
        State gameState = DemoGameBuilder.buildGame(10, interfaces);
        GameEngine gameThr = new GameEngine(gameState);
        changeRecord = gameThr.getStateChangeRecord();
        gameThr.run();
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
    	Player player;
    	for(ArrayList<Change> turnChanges : playersTurns){
    		if(turnChanges.size() <= 0)
    			fail();
    		player = turnChanges.get(0).getActingPlayer();
    		for(Change change : turnChanges){
    			assertEquals(change.getActingPlayer(), player);
    		}
    	}
    }
    
    @Test
    public void printTest(){
    	changeRecord.printAllChanges();
    }
    
   
    
}
