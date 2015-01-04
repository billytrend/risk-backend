/**
 * 
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author 120011995
 * @category Class which acts as a client to connect to the multi-threaded Server specified in Server class
 */
public class Client {
	
	
	public static void main(String[] args) {
		String clientName = args[0];
		new Client().clientConnection(clientName); 
	}

	/**
	 * Method which creates client connections and 
	 * communicates messages to Server class.
	 */
	public void clientConnection(String clientName){
		int port = 4444;
		try {
			//create Client Socket and writer to used to send info to Server
			Socket clientSocket = new Socket("localhost", port);
			PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader clientReader = new BufferedReader(new InputStreamReader(System.in));
			//loop to retrieve user data and then send to Server
			while(true){
				String readerInput = clientReader.readLine();
				clientWriter.println(clientName + " :" + readerInput);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
