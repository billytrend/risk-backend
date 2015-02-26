package GeneralUtils.Serialisers;

import org.javatuples.Pair;


import GameState.Continent;
import GameState.State;
import GameState.Territory;
import GameUtils.TerritoryUtils;
import com.google.gson.*;
public class BoardSerialiser {
	
	public JsonElement serializeMap(State state){
		JsonObject map = new JsonObject();
		map.add("countries", serializeCountries(state));
		map.add("continents", serializeContinents(state));
		map.add("borders", serializeBorders(state));
		return map;
	}
	public JsonElement serializeContinents(State state){
		JsonObject continents = new JsonObject();
		for(Continent c : state.getContinents()){
			JsonArray territories = new JsonArray();
			for(Territory t : c.getTerritories()){
				territories.add(new JsonPrimitive(t.getId()));
			}
			continents.add(c.getId(), territories);
		}
		return continents;
	}
	public JsonElement serializeBorders(State state){
		JsonArray borders = new JsonArray();
		for (Pair<Territory, Territory> p : TerritoryUtils.getAllBorders(state)){ 	
			JsonArray pair = new JsonArray();
			pair.add(new JsonPrimitive(p.getValue0().getId()));
			pair.add(new JsonPrimitive(p.getValue1().getId()));
			borders.add(pair);
		}
		return borders;
	}

	public JsonElement serializeCountries(State state){
		JsonArray countries = new JsonArray();
		for (Territory t : TerritoryUtils.getAllTerritories(state) ) {
			countries.add(new JsonPrimitive(t.getId()));
		}
		return countries;
	}
}
