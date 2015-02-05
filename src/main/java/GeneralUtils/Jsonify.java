package GeneralUtils;

import GameState.State;
import GeneralUtils.Serialisers.GameStateSerialiser;
import GeneralUtils.Serialisers.LobbySerialiser;
import LobbyServer.LobbyState.Lobby;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        return new Gson().fromJson(s, c);
    }

}
