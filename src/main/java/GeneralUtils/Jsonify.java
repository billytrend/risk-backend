package GeneralUtils;

import java.util.Arrays;

import GameState.State;
import GeneralUtils.Serialisers.GameStateSerialiser;
import GeneralUtils.Serialisers.LobbySerialiser;
import LobbyServer.LobbyState.Lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class Jsonify {

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Lobby.class, new LobbySerialiser())
            .registerTypeAdapter(State.class, new GameStateSerialiser())
            .create();
            
    // the idea is that this maybe overridden to handle certain objects
    public static String getObjectAsJsonString(Object o) {
        return gson.toJson(o);
    }

//    public static String getObjectAsJsonString(Lobby lobby) {
//        return new Gson().toJson(lobby);
//    }

    public static Object getJsonStringAsObject(String s, Class c) {
        Object ob =  new Gson().fromJson(s, c);
        return ob;
    }
    
    public static Object getJsonFromCommand(String command, Class c){
    	message msg = (message) getJsonStringAsObject(command, message.class);
    	Object ob = getJsonStringAsObject(msg.jsonContent, c);
    	return ob;
    }
    
    public static String getObjectAsCommand(Object ob){
    	String str = getObjectAsJsonString(ob);
    	message msg = new message();
    	msg.jsonContent = str;
    	String result = getObjectAsJsonString(msg);
    	return result;
    }

 
    public static class message{
    	public String jsonContent;
    }
}
