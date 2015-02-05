package GeneralUtils.Serialisers;

import GameState.State;
import GameState.Territory;
import GameUtils.TerritoryUtils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

// example state
//{
//  "map": {
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
// },

public class GameStateSerialiser implements JsonSerializer<State> {

    @Override
    public JsonElement serialize(State state, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        JsonArray arr = new JsonArray();
   
        for (Territory t : TerritoryUtils.getAllTerritories(state) ) {
        	arr.add(new JsonPrimitive(t.getId()));
        }
        
        jsonObject.add("countries", arr);
        return jsonObject;
    }
}
