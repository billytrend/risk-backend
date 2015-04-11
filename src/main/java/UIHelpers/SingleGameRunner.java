package UIHelpers;

import GameBuilders.RiskMapGameBuilder;
import GameState.State;
import LobbyServer.SingleGameServer;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

import java.io.IOException;

public class SingleGameRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        SingleGameRunner s = new SingleGameRunner();

        s.start();
    }

    public void start() throws IOException, InterruptedException {

        Thread t = new Thread(new WebpackRunner());
        t.start();

        State gameState = RiskMapGameBuilder.buildGame(new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface()});
        SingleGameServer s = new SingleGameServer(8887, gameState, new PlayerInterface[] { new DumbBotInterface(), new DumbBotInterface()});

        t.join();

    }

}