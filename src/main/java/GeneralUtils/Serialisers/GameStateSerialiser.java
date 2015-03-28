package GeneralUtils.Serialisers;

import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.TerritoryUtils;
import com.google.gson.*;

import java.lang.reflect.Type;

// example state
//{
//  "map": {
//	"continents":{
//		"Europe":["great_britain", "iceland", "northern_europe"],
//	"continent_values":{
//		"Europe":7, "Australia":2
//  "countries": ["great_britain", "iceland", "northern_europe", "southern_europe", "western_europe", "scandinavia", "ukraine"],
//  "borders": [
//      ["great_britain", "iceland"],
//      ["great_britain", "scandinavia"],
//      ["great_britain", "northern_europe"],
//      ["great_britain", "western_europe"],
//      ["western_europe", "southern_europe"],
//      ["western_europe", "northern_europe"],
//      ["ukraine", "northern_europe"],
//      ["ukraine", "southern_europe"],
//      ["ukraine", "scandinavia"],
//      ["sime_land", "scandinavia"],
//      ["_land", "scandinavia"]
//  ]
//  }
//	
// },

public class GameStateSerialiser implements JsonSerializer<State> {

    @Override
    public JsonElement serialize(State state,Type type, JsonSerializationContext jsonSerializationContext) {
        
    	JsonObject jsonObject = new JsonObject();
    	JsonObject map = new JsonObject();
        map.add("countries", jsonSerializationContext.serialize(TerritoryUtils.getAllCountryNames(state)));
        map.add("borders", jsonSerializationContext.serialize(TerritoryUtils.getAllBorderPairs(state)));
        jsonObject.add("map", map);
        jsonObject.add("ownerships", getOwnerships(state, jsonSerializationContext));
        jsonObject.add("players", serializePlayers(state, jsonSerializationContext));
        jsonObject.add("changeType", new JsonPrimitive("State"));
        return jsonObject;
    }
    
    //gets each player and all the territories they own
    public JsonElement serializePlayers(State state, JsonSerializationContext jsonSerializationContext){
    	JsonObject players = new JsonObject();

        for (Player p : state.getPlayers()) {
            players.add(p.getId(), jsonSerializationContext.serialize(p));
        }

        return players;
    }

    public static JsonElement getOwnerships(State state, JsonSerializationContext jsonSerializationContext) {
        JsonObject ownerships = new JsonObject();
        for (Territory t : TerritoryUtils.getUnownedTerritories(state)) {
            JsonObject ownerObj = new JsonObject();
            ownerObj.add("player", new JsonNull());
            ownerObj.add("armies", new JsonPrimitive(0));
            ownerships.add(t.getId(), ownerObj);
        }
        for(Player p : state.getPlayers()) {
            for (Territory t : TerritoryUtils.getPlayersTerritories(p)) {
                JsonObject ownerObj = new JsonObject();
                ownerObj.add("player", new JsonPrimitive(p.getId()));
                ownerObj.add("armies", new JsonPrimitive(ArmyUtils.getNumberOfArmiesOnTerritory(p, t)));
                ownerships.add(t.getId(), ownerObj);
            }
        }
        return ownerships;
    }

    
}
