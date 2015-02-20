package GeneralUtils;

import LobbyServer.LobbyState.Lobby;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class LobbySerialise implements JsonDeserializer<Lobby> {

	@Override
	public Lobby deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException {
		return null;
	}
}
