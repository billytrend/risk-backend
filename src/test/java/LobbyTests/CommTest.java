package LobbyTests;

import org.junit.Test;

import java.net.UnknownHostException;

public class CommTest {

//    @Test
//    public void testJsonifying() {
//        Log.DEBUG();
//        ArmyRequest a = new ArmyRequest();
//        ArmyResponse c = new ArmyResponse();
//        DiceNumberRequest d = new DiceNumberRequest();
//        DiceNumberResponse e = new DiceNumberResponse();
//        TerritoryRequest h = new TerritoryRequest();
//        h.possibles = new HashSet<Territory>();
//        h.possibles.add(new Territory("test", 1));
//        TerritoryResponse i = new TerritoryResponse();
//        Log.debug(Jsonify.getObjectAsJsonString(a));
//        Log.debug(Jsonify.getObjectAsJsonString(c));
//        Log.debug(Jsonify.getObjectAsJsonString(d));
//        Log.debug(Jsonify.getObjectAsJsonString(e));
//        Log.debug(Jsonify.getObjectAsJsonString(h));
//        Log.debug(Jsonify.getObjectAsJsonString(i));
//    }

    @Test
    public void testSingleGameRun() throws UnknownHostException {
    }

//    @Test
//    public void testMoreJsonifying() {
//        Log.DEBUG();
//        Change b = new ArmyMovement(new Player(null, 3), new Territory("lol"), new Territory("lola"), 3, PlayState.BEGINNING_STATE);
//        Change c = new ArmyPlacement(new Player(null, 3), new Territory("lol"), 2, PlayState.BEGINNING_STATE);
//        Change d = new FightResult(new Player(null, 3), new Player(null, 3), new Territory("lol"), new Territory("lola"));
//        Change e = new PlayerRemoval(new Player(null, 3), new Player(null, 3), DemoGameBuilder.buildGame(3, new PlayerInterface[]{null, null}));
//        Log.debug(Jsonify.getObjectAsJsonString(b));
//        Log.debug(Jsonify.getObjectAsJsonString(c));
//        Log.debug(Jsonify.getObjectAsJsonString(d));
//        Log.debug(Jsonify.getObjectAsJsonString(e));
//    }

}
