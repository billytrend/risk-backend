/**
 * 
 */
package PeerServer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 120011995
 *
 */
public class Multithreaded_TCP_Server implements Runnable  {

	private int portNumber = 4444;
	private ServerSocket serverSocket;

	public Multithreaded_TCP_Server(){
		this.portNumber = portNumber;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Multithreaded_TCP_Server server = new Multithreaded_TCP_Server();
		new Thread(server).start();
	}


	@Override
	public void run() {
		int clientNumber = 1;
		if (openServerSocket()) {
			System.out
			.println("Server up and listening on Port: " + portNumber);
			while (true) {
				try {
					//begin a connection
					Socket clientSocket = serverSocket.accept();
					//create a server thread for the new connection
					new Server_Thread(clientSocket,clientNumber);
					//increment client number
					clientNumber++;
					
					
				} catch (IOException e) {
					try {
						serverSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
	}		

	/**
	 * Opens server socket
	 * @return true if socket was opened correctly, false if failed
	 */
	public boolean openServerSocket() {
		try {
			serverSocket = new ServerSocket(portNumber);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
