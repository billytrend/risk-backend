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

    ArrayList<Player> players = new ArrayList<Player>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(0, new Player(new DumbBotInterface()));
        players.add(1, new Player(new DumbBotInterface()));
        players.add(2, new Player(new DumbBotInterface()));
        players.add(3, new Player(new DumbBotInterface()));
        players.add(4, new Player(new DumbBotInterface()));

        SingleGameRunner s = new SingleGameRunner(players);
        s.start();
    }


    public SingleGameRunner(ArrayList<Player> players) {
        this.players = players;
    }

    public void start() throws IOException, InterruptedException {

        Thread t = new Thread(new WebpackRunner());
        t.start();

        State gameState = RiskMapGameBuilder.buildGame(new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(), new DumbBotInterface(), new DumbBotInterface()});
        SingleGameServer s = new SingleGameServer(8887, gameState, players);

        t.join();

    }

}