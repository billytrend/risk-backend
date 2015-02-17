/**
 * 
 */
package PeerServer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * @author 120011995
 * @category Class to hold the PeerServer for our multi-player risk implementation
 */
public class Server {

	private Thread thread;
	public CountDownLatch recevingMessage = new CountDownLatch(0);
	
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
	
	public void routeMessage(String jsonString) {
		// if busy with other message
		try {
			recevingMessage.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// if not
		this.recevingMessage = new CountDownLatch(1);
		// parse message
		// if host, distribute message
		// send it to the relevant player interface
		// relevantPlayerInterface.setPlayerResponse(jsonObject);
		this.recevingMessage.countDown();
	}
	
	
	
}