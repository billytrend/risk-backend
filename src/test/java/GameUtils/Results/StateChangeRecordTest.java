package GameUtils.Results;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameEngine.GameEngine;
import GameState.State;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

public class StateChangeRecordTest {

    @Test
    public void recordTest() {
	    PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()};
        State gameState = DemoGameBuilder.buildGame(2, 10, interfaces);
        GameEngine gameThr = new GameEngine(gameState);
        gameThr.run();
        
        assertTrue(StateChangeRecord.getSize() > 0);
        
        Iterator<StateChange> it = StateChangeRecord.getIterator();
        StateChange change;
        while(it.hasNext()){
        	change = it.next();
        	assertTrue((change.getArmyMovements().size() > 0) || (change.getDestroyedArmies().size() > 0));
        }
        System.out.println(StateChangeRecord.getSize());
    }
    
    
}
