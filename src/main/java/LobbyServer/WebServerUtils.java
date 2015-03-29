package LobbyServer;

import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GameComms.*;
import LobbyServer.LobbyState.ObjectFromClient.GenericTypingObject;

import java.util.HashMap;

public class WebServerUtils {

    public static HashMap<String, Class> availableClasses = new HashMap<String, Class>();
    
    public static void fillAvailableClases() {
        availableClasses.put("ArmyRequest", ArmyRequest.class);
        availableClasses.put("ArmyResponse", ArmyResponse.class);
        availableClasses.put("DiceNumberRequest", DiceNumberRequest.class);
        availableClasses.put("DiceNumberResponse", DiceNumberResponse.class);
        availableClasses.put("Request", Request.class);
        availableClasses.put("Response", Response.class);
        availableClasses.put("TerritoryRequest", TerritoryRequest.class);
        availableClasses.put("TerritoryResponse", TerritoryResponse.class);
    }
    
    public static ClientMessage getMessageObject(String s) {
        
        if (availableClasses.size() == 0) {
            fillAvailableClases();
        }
        
        GenericTypingObject o = (GenericTypingObject) Jsonify.getJsonStringAsObject(s, GenericTypingObject.class);
        Class stringClass = availableClasses.get(o.commandType);
        
        return (ClientMessage) Jsonify.getJsonStringAsObject(s, stringClass);
    }

}
