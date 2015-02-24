package GeneralUtils.Serialisers;

import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.CardUtils;
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
//        BoardSerialiser JsonBoard = new BoardSerialiser();
//        map = (JsonObject) JsonBoard.serializeMap(state);
        jsonObject.add("map", map);
        jsonObject.add("players", serializePlayers(state));

        return jsonObject;
    }
    
    //gets each player and all the territories they own
    public JsonElement serializePlayers(State state){
    	JsonObject players = new JsonObject();
    	JsonObject playersItems = new JsonObject();
    	
        for(Player p : state.getPlayers()){
        	JsonArray territories = new JsonArray();
        	for(Territory t : TerritoryUtils.getPlayersTerritories(p)){
        		territories.add(new JsonPrimitive(t.getId()));
        	}
        	JsonArray cards = new JsonArray();
        	for(Card c : CardUtils.getPlayersCards(state, p)){
        		cards.add(new JsonPrimitive(c.getType().toString()));
        	}
        	playersItems.add("cards", cards);
        	playersItems.add("territories", territories);
        	players.add(p.getId(), playersItems);
        }
        return players;
    }
    

    
}
