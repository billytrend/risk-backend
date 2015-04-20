package AI;

import static org.junit.Assert.*;

import org.junit.Test;

import GameBuilders.DemoGameBuilder;
import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.State;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;

public class TestGameSetup {
	public void setup(PlayerInterface[] players)throws InterruptedException {
    PlayerInterface[] interfaces = new PlayerInterface[]{new DumbBotInterface(), new DumbBotInterface(),
    		new DumbBotInterface(), new DumbBotInterface()};

    State gameState = RiskMapGameBuilder.buildGame(interfaces);

    GameEngine gameThr = new GameEngine(gameState);
    Thread gameThread = new Thread(gameThr);
    gameThread.start();
    gameThread.join();
    gameState.print();
	}
}
