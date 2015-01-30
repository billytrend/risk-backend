package LobbyServer.LobbyState;

import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.CardSelection;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerInterface;
import org.java_websocket.WebSocket;

import java.util.HashSet;

public class PlayerConnection implements PlayerInterface  {
    
    private Player playerState;
    private WebSocket connection;

    public PlayerConnection(WebSocket connection) {
        this.playerState = new Player(this, 0, 100); // this last number to be changed
        this.connection = connection;
    }

    @Override
    public DiceSelection getNumberOfDice(Player player, int max) {
        
        return null;
    }

    @Override
    public CountrySelection getTerritory(Player player,
                                         HashSet<Territory> possibles, boolean canResign) {

        return null;
    }

    @Override
    public ArmySelection getNumberOfArmies(Player player, int playersUndeployedArmies) {
        return null;
    }

    @Override
    public void giveCard(Player player, Card card) {
        return;
    }

    @Override
    public CardSelection getCardOptions() {
        return null;
    }
}
