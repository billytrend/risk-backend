/**
 * 
 */
package PeerServer.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author 120011995
 * @category Class which acts as a client to connect to 
 * the multi-threaded PeerServer specified in PeerServer class
 */

public class Client implements Runnable {
	//socket conencted to server
	private Socket socket;
	//streams used to communicate to server come from socket
	private DataOutputStream dout;
	private DataInputStream din;
	private final static int port = 4444;

	public static void main(String[] args) {
		String clientName = args[0];
		new Client("localhost", port); 
	}

	//Constructor
	public Client(String host, int port){
		try {
			socket = new Socket(host, port);
			System.out.println("connected to " + socket);

			//Add the sockets streams to data sockets
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			// Start a background thread for receiving messages
			new Thread(this).start();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Gets called when the user types something
	private void processMessage(String message) {
		try {
			// Send it to the server
			dout.writeUTF(message);
		} catch( IOException ie ) { System.out.println( ie ); }
	}

	@Override
	public void run() {
		try {
			// Receive messages one-by-one, forever
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(System.in));
			String messageFromClient = fromClient.readLine();
			processMessage(messageFromClient);
			while (true) {
				// Get the next message
				String message = din.readUTF();
				System.out.println(message);
			}
		} catch( IOException ie ) { System.out.println( ie ); }
	}
}

