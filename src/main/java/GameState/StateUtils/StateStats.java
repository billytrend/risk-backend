package GameState.StateUtils;

import GameState.State;

/**
 * Created by bt on 30/12/2014.
 */
public class StateStats {

    public static int countPlayers(State state) {
        return state.getPlayers().size();
    }

    public static int countTerritories(State state) {
        return MapUtils.getAllTerritories(state).size();
    }

}
