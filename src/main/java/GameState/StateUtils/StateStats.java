package GameState.StateUtils;

import GameState.StateClasses.State;

public class StateStats {

    /**
     * *
     * @param state
     * @return
     */
     public static int countPlayers(State state) {
     return state.getPlayers().size();
     }

     /**
     *
     * * @param state
     * @return
     */
    public static int countTerritories(State state) {
        return MapUtils.getAllTerritories(state).size();
    }

}
