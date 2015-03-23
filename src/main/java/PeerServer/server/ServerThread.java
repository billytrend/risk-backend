/**
 * 
 */
package PeerServer.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * @author 120011995
 * @category Class to implement multithreading in TCP PeerServer
 */
public class ServerThread extends Thread {
	//server to connect to
	private Multithreaded_TCP_Server server;
	// The Socket connected to our client
	private Socket socket;

	// Constructor
	public ServerThread( Multithreaded_TCP_Server server, Socket socket) {
		// Save the parameters
		this.server = server;	
		this.socket = socket;
		// Start up the thread
		start();
	}

	public void run() {
		try {
			//the client is using a DataOutputStream to write to server
			DataInputStream din = new DataInputStream(socket.getInputStream());
			while (true) {
				String message = din.readUTF();
				System.out.println( "Sending " + message);
				//server send it to all clients
				server.sendToAll(message);
			}
		} catch( EOFException ie ) {
		} catch( IOException ie ) {
			ie.printStackTrace();
		} finally {
			// The connection is closed for one reason or another,
			// so have the server dealing with it
			server.removeConnection(socket);
		}
	}
}
