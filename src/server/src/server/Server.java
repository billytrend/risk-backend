/**
 * 
 */
package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 120011995
 * @category Class to hold the Server for our multi-player risk implementation
 */
public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Server().runServer();
	}

	/**
	 * Method which invokes the setup and running of the multi-threaded Server
	 */
	public void runServer() {
		int port = 4444;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server up and ready for connections on port: " + port);
			//enables multithreading via blocking and waiting for clients
			while(true){
				Socket client = serverSocket.accept();
				new ServerThread(client).start();
			}
		} catch (IOException e) {
			System.out.println("Could not create Server Socket");
			e.printStackTrace();
		}
	}
}