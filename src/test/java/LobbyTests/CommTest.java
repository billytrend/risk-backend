package LobbyTests;

import GameState.Territory;
import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.*;
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
    
}
