package UIHelpers;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.SingleGameServer;
import PlayerInput.DumbBotInterface;

import java.io.IOException;
import java.util.ArrayList;

public class SingleGameRunner {

    ArrayList<Player> players = new ArrayList<Player>();
    State gameState;

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<Player> players = new ArrayList<Player>();
        State gameState = RiskMapGameBuilder.buildGame(null);

        players.add(0, new Player(new DumbBotInterface()));
        players.add(1, new Player(new DumbBotInterface()));

        SingleGameRunner s = new SingleGameRunner(players, gameState);
        s.start();
    }


    public SingleGameRunner(ArrayList<Player> players, State gameState) {
        this.players = players;
        this.gameState = gameState;
    }

    public void start() throws IOException, InterruptedException {

        Thread t = new Thread(new WebpackRunner());
        t.start();

        SingleGameServer s = new SingleGameServer(8887, gameState, players);

        t.join();

    }

}