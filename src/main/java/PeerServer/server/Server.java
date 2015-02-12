/**
 * 
 */
package PeerServer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

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

	private Thread thread;
	public CountDownLatch CDL = new CountDownLatch(1);


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
		System.out.println("Please enter the port you would like to run the game from!");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int port = 4444; //4444 for default
		String portFromClient;
		
		try {
			portFromClient = br.readLine();
			port = Integer.parseInt(portFromClient);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Socket connectionSocket = null;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("PeerServer up and ready for connections on port: " + port);
			//enables multi-threading via blocking and waiting for clients
			while (true){
				connectionSocket = serverSocket.accept();
				new Thread(new ServerThread(connectionSocket)).start();
			}
		} catch (IOException e) {
			System.out.println("Could not create PeerServer Socket");
			e.printStackTrace();
		}
	}

	public void stopServer(){
		try {
			//thread.server.close();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} //catch (IOException e) {
			//e.printStackTrace();
		//}
	}

	public void send(){
		//Requests and sends the JSON to a player

	}

	public void sendToAll(){
		//Sends a request or JSON message to all players
		//for loop using send() method
	}

	public void decode(){
		//Receive other players messages then decode
		//decrement countdown latch
		CDL.countDown();
	}

	public void waitForResponse(){
		//waits players response messages
		try {
			CDL.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		return 0;
	}

	@Override
	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {
		return null;
	}

	@Override
	public int getNumberOfArmies(Player player, int max, RequestReason reason) {
		return 0;
	}

	@Override
	public void giveCard(Player player, Card card) {		
	}

	@Override
	public Card getCardOptions() {
		return null;
	}
}