package GameEngine;

/**
 * Enum representing all possible states of the game.
 * Vital for the programs execution.
 *
 */
public enum PlayState {
    BEGINNING_STATE,
    FILLING_EMPTY_COUNTRIES,
    USING_REMAINING_ARMIES,
    PLAYER_BEGINNING_TURN,
    PLAYER_PLACING_ARMIES,
  //  PLAYER_CONVERTING_CARDS,
    PLAYER_INVADING_COUNTRY,
    PLAYER_MOVING_ARMIES,
    PLAYER_ENDED_GO, 
    END_GAME
}