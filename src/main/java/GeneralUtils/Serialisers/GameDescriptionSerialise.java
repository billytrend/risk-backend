package GeneralUtils.Serialisers;

import LobbyServer.LobbyState.GameDescription;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GameDescriptionSerialise implements JsonSerializer<GameDescription> {
    
    @Override
    public JsonElement serialize(GameDescription lobby, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();

//        private final String id;
//        private final int maxPlayers;
//        private final ArrayList<String> AIs = new ArrayList<String>();
//        private ArrayList<Player> players = new ArrayList<Player>();
//        private ArrayList<PlayerInterface> ghosts = new ArrayList<PlayerInterface>();
//        private State gameState;
//        private transient Thread thread;

        jsonObject.add("maxPlayers", new JsonPrimitive(lobby.getMaxPlayers()));
        jsonObject.add("players", jsonSerializationContext.serialize(lobby.getPlayers()));
        jsonObject.add("numberOfGhosts", jsonSerializationContext.serialize(lobby.getPlayers()));
        return jsonObject;
    }
}
