package GeneralUtils.Serialisers;

import GameState.State;
import GameState.Territory;
import GameUtils.TerritoryUtils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.javatuples.Pair;

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
    public JsonElement serialize(State state,Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        JsonArray countries = new JsonArray();
        
        for (Territory t : TerritoryUtils.getAllTerritories(state) ) {
        	countries.add(new JsonPrimitive(t.getId()));
        }
        jsonObject.add("countries", countries);
        
        JsonArray borders = new JsonArray();
        
        for (Pair<Territory, Territory> p : TerritoryUtils.getAllBorders(state)){ 		
        	borders.add(new JsonPrimitive(p.getValue0().getId()+ "," + p.getValue1().getId()));
        }
        
        jsonObject.add("borders", borders);

        return jsonObject;
    }
}
