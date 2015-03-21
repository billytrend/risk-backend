package LobbyTests;

import GameBuilders.DemoGameBuilder;
import GameEngine.PlayState;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.*;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.*;
import PlayerInput.PlayerInterface;
import com.esotericsoftware.minlog.Log;
import org.junit.Test;

import java.util.HashSet;

public class CommTest {
    
    @Test
    public void testJsonifying() {
        Log.DEBUG();
        ArmyRequest a = new ArmyRequest();
        ArmyResponse c = new ArmyResponse();
        DiceNumberRequest d = new DiceNumberRequest();
        DiceNumberResponse e = new DiceNumberResponse();
        TerritoryRequest h = new TerritoryRequest();
        h.possibles = new HashSet<Territory>();
        h.possibles.add(new Territory("test"));
        TerritoryResponse i = new TerritoryResponse();
        Log.debug(Jsonify.getObjectAsJsonString(a));
        Log.debug(Jsonify.getObjectAsJsonString(c));
        Log.debug(Jsonify.getObjectAsJsonString(d));
        Log.debug(Jsonify.getObjectAsJsonString(e));
        Log.debug(Jsonify.getObjectAsJsonString(h));
        Log.debug(Jsonify.getObjectAsJsonString(i));
    }
    
    @Test
    public void testMoreJsonifying() {
        Log.DEBUG();
        Change b = new ArmyMovement(new Player(null, 3), new Territory("lol"), new Territory("lola"), 3, PlayState.BEGINNING_STATE);
        Change c = new ArmyPlacement(new Player(null, 3), new Territory("lol"), 2, PlayState.BEGINNING_STATE);
        Change d = new FightResult(new Player(null, 3), new Player(null, 3), new Territory("lol"), new Territory("lola"));
        Change e = new PlayerRemoval(new Player(null, 3), new Player(null, 3), DemoGameBuilder.buildGame(3, new PlayerInterface[]{null, null}));
        Log.debug(Jsonify.getObjectAsJsonString(b));
        Log.debug(Jsonify.getObjectAsJsonString(c));
        Log.debug(Jsonify.getObjectAsJsonString(d));
        Log.debug(Jsonify.getObjectAsJsonString(e));
    }

}
