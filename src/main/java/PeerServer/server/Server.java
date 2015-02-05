/**
 * 
 */
package PeerServer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerInterface;

/**
 * @author 120011995
 * @category Class to hold the PeerServer for our multi-player risk implementation
 */
public class Server implements PlayerInterface {

	private ServerThread thread;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Server().runServer();
	}

	/**
	 * Method which invokes the setup and running of the multi-threaded PeerServer
	 */
	public void runServer() {
		int port = 4444;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("PeerServer up and ready for connections on port: " + port);
			//enables multithreading via blocking and waiting for clients
			thread = new ServerThread(serverSocket);
			thread.start();
		} catch (IOException e) {
			System.out.println("Could not create PeerServer Socket");
			e.printStackTrace();
		}
	}
	
	public void stopServer(){
		try {
			thread.client.close();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createJSONString(){
		//Creates the message to be sent to other players
	}
	
	public void send(){
		//Sends the JSON to other players
		
	}
	
	public void decode(){
		//recieve other players messages then decode
	}

	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void giveCard(Player player, Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Card getCardOptions() {
		// TODO Auto-generated method stub
		return null;
	}
}