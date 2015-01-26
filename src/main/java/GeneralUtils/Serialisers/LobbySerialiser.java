package GeneralUtils.Serialisers;

import GameBuilders.DemoGame;
import GameState.State;
import LobbyServer.LobbyState.Lobby;
import com.google.gson.*;
import org.json.simple.JSONArray;

import java.lang.reflect.Type;

public class LobbySerialiser implements JsonSerializer<Lobby> {
    
    @Override
    public JsonElement serialize(Lobby lobby, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();

        JSONArray games = new JSONArray();
        jsonObject.add("lol", jsonSerializationContext.serialize(DemoGame.buildGame(2, 3)));
        
        for (State s : lobby.getCurrentGames()) {
            games.add(jsonSerializationContext.serialize(s));
        }

        return jsonObject;
    }
}
