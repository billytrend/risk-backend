package GeneralUtils.Serialisers;

import LobbyServer.LobbyState.Lobby;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LobbySerialiser implements JsonSerializer<Lobby> {
    
    @Override
    public JsonElement serialize(Lobby lobby, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("lobbyUpdate", new JsonPrimitive(true));

        jsonObject.add("startedGames", jsonSerializationContext.serialize(lobby.getStartedGames()));
        jsonObject.add("unstartedGames", jsonSerializationContext.serialize(lobby.getunstartedGames()));
        return jsonObject;
    }
}
