package UIHelpers;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import LobbyServer.SingleGameServer;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import java.io.IOException;
import java.util.ArrayList;

public class SingleGameRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        SingleGameRunner s = new SingleGameRunner();

        s.start();
    }

    public void start() throws IOException, InterruptedException {

        Thread t = new Thread(new WebpackRunner());
        t.start();

        State gameState = RiskMapGameBuilder.buildGame(new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()});
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(0, new Player(new DumbBotInterface()));
        players.add(1, new Player(new DumbBotInterface()));
        SingleGameServer s = new SingleGameServer(8887, gameState, players);

        t.join();

    }

}