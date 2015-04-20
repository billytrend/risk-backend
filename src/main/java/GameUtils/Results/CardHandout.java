package GameUtils.Results;

import GameState.Card;
import GameState.Player;
import GameState.State;
import GameUtils.CardUtils;

public class CardHandout extends Change {

    private Card card;

    public CardHandout(String actingPlayerId) {
        super(actingPlayerId);
    }

    @Override
    public void applyChange(State state) {
        Player player = state.lookUpPlayer(actingPlayerId);
        card = CardUtils.givePlayerRandomCard(state, player);
    }
}
