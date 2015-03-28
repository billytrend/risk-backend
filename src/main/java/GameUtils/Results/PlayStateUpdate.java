package GameUtils.Results;

import GameEngine.PlayState;
import GameState.State;

public class PlayStateUpdate extends Change {

    public final PlayState playState;

    public PlayStateUpdate(String actingPlayerId, PlayState playState) {
        super(actingPlayerId);
        this.playState = playState;
    }

    @Override
    public void applyChange(State state) {

    }
}
