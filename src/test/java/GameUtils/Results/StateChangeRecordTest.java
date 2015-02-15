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

	
	@Before
	public void setUp(){
		PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
        State gameState = DemoGameBuilder.buildGame(2, 10, interfaces);
        GameEngine gameThr = new GameEngine(gameState);
        gameThr.run();
	}
	
    @Test
    public void recordSizeTest() {
   	
        assertTrue(StateChangeRecord.getSize() > 0);
        assertTrue(StateChangeRecord.getPlayersTurns().size() > 0);
        assertTrue(StateChangeRecord.getPlayersTurns().size() < StateChangeRecord.getSize());
        
        Iterator<StateChange> it = StateChangeRecord.getIterator();
        StateChange change;
        while(it.hasNext()){
        	change = it.next();
        	assertTrue((change.getArmyMovements().size() > 0) || (change.getDestroyedArmies().size() > 0));
        }
        
    }
    
    @Test
    public void getPlayersTurnsTest(){
    	ArrayList<ArrayList<StateChange>> playersTurns = StateChangeRecord.getPlayersTurns();
    	Player player;
    	for(ArrayList<StateChange> turnChanges : playersTurns){
    		if(turnChanges.size() <= 0)
    			fail();
    		player = turnChanges.get(0).getActingPlayer();
    		for(StateChange change : turnChanges){
    			assertEquals(change.getActingPlayer(), player);
    			assertTrue((change.getArmyMovements().size() > 0) || (change.getDestroyedArmies().size() > 0));
    		}
    	}
    }
    
    @Test
    public void printTest(){
    	StateChangeRecord.printAllChanges();
    }
    
    
    
}
