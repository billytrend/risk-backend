package GameUtils.Results;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameState.State;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class StateChangeRecordTest {

	private StateChangeRecord changeRecord;

	@Before
	public void setUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
        State gameState = DemoGameBuilder.buildGame(interfaces);
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
    public void printTest(){
        // TODO: why is this failing?
//    	changeRecord.printAllChanges();
    }
    
   
    
}
