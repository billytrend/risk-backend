package GeneralUtils.Serialisers;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import LobbyServer.LobbyState.Lobby;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import com.google.gson.*;

import org.json.simple.JSONArray;

import java.lang.reflect.Type;

public class LobbySerialiser implements JsonSerializer<Lobby> {

	@Override
	public JsonElement serialize(Lobby lobby, Type type,
			JsonSerializationContext jsonSerializationContext) {

		JsonObject jsonObject = new JsonObject();

		JSONArray games = new JSONArray();
		PlayerInterface[] interfaces = new PlayerInterface[] {
				new DumbBotInterface(), new DumbBotInterface() };
		jsonObject.add("lol", jsonSerializationContext
				.serialize(DemoGameBuilder.buildGame(2, 3, interfaces)));

		for (State s : lobby.getCurrentGames()) {
			games.add(jsonSerializationContext.serialize(s));
		}

		return jsonObject;
	}
}
