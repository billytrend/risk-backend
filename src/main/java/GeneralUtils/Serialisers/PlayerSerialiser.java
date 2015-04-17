package GeneralUtils.Serialisers;

import GameState.Player;
import GameUtils.ArmyUtils;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PlayerSerialiser implements JsonSerializer<Player> {

    @Override
    public JsonElement serialize(Player player, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject playerObject = new JsonObject();
        playerObject.add("id", new JsonPrimitive(player.getId()));
        playerObject.add("colour", new JsonPrimitive(player.getColour()));
        playerObject.add("undeployedArmies", new JsonPrimitive(ArmyUtils.getUndeployedArmies(player).size()));
        playerObject.add("totalArmies", new JsonPrimitive(player.getArmies().size()));
        return playerObject;
    }
}
