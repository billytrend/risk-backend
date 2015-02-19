/**
 * 
 */
package PeerServer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * @author 120011995
 * @category Class to hold the PeerServer for our multi-player risk implementation
 */
public class Server {

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
			debug("PeerServer up and ready for connections on port: " + port);
			//enables multithreading via blocking and waiting for clients
			thread = new ServerThread(serverSocket);
			thread.start();
		} catch (IOException e) {
			debug("Could not create PeerServer Socket");
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
	
}