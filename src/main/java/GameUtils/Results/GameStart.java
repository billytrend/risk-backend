package GameUtils.Results;

import GameEngine.PlayState;
import GameState.State;

public class GameStart extends Change {

    public final PlayState playState;
    public final State gameState;

    public GameStart(String actingPlayerId, PlayState playState, State gameState) {
        super(actingPlayerId);
        this.playState = playState;
        this.gameState = gameState;
    }

    @Override
    public void applyChange(State state) {

    }
}
