/**
 * 
 */
package PeerServer.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author 120011995
 *
 */
public class Multithreaded_TCP_Server {

	private int portNumber = 4444;
	private ServerSocket serverSocket;
	//to hold all of the sockets connected output streams
	private Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	private static Logger log = LogManager.getLogger(Multithreaded_TCP_Server.class.getName());


	public Multithreaded_TCP_Server(){
		listen(portNumber);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Multithreaded_TCP_Server();
	}
 
	/**
	 * Sets server up to listen for client conenctions. 
	 * @param portNumber
	 */
	public void listen(int portNumber) {
		//int clientNumber = 1;
		if (openServerSocket()) {
			System.out
			.println("Server up and listening on Port: " + portNumber);
			while (true) {
				try {
					//begin a connection
					Socket clientSocket = serverSocket.accept();
					DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
					outputStreams.put(clientSocket, dout );
					System.out.println("Recieved connection from "+ 
							clientSocket.getInetAddress()+ " on port "+ clientSocket.getPort());
					log.warn("YOLO");
					log.info("HERRROR");
					log.error("lol");

					//create a thread for the new connection
					new ServerThread(this,clientSocket);
					//increment client number
					//clientNumber++;
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

	// Get an enumeration of all the OutputStreams, one for each client connected 
	public Enumeration<DataOutputStream> getOutputStreams() {
		return outputStreams.elements();
	}

	/**
	 * We synchronize on this because another thread might be
	 * calling removeConnection() and this would screw us up
	 * as we tried to walk through the list
	 * @param message
	 */
	public void sendToAll( String message ) {
		synchronized( outputStreams ) {
			// For each client
			for (Enumeration<DataOutputStream> e = getOutputStreams(); e.hasMoreElements(); ) {
				//get the output stream
				DataOutputStream dout = (DataOutputStream)e.nextElement();
				//and send the message
				try {
					dout.writeUTF( message );
				} catch( IOException ie ) { System.out.println( ie ); }
			}
		}
	}


	/**
	 * 	Remove a socket, and it's corresponding output stream, from our
	 *	list. This is usually called by a connection thread that has
	 *	discovered that the connection to the client is dead.
	 * @param s
	 */
	public void removeConnection( Socket s ) {
		// Synchronize so we don't mess up sendToAll() while it walks
		// down the list of all output streams
		synchronized( outputStreams ) {
			System.out.println( "Removing connection to " + s );
			// Remove it from our hashtable/list
			outputStreams.remove( s );
			// Make sure it's closed
			try {
				s.close();
			} catch( IOException ie ) {
				System.out.println( "Error closing " + s );
				ie.printStackTrace();
			}
		}
	}
	
	
	/**
	 * @param test
	 * @return true if string is valid JSON
	 * 			false otherwise
	 */
	public boolean isJsonStringValid(String test) {
	    try {
	    	JsonObject o = new JsonParser().parse(test).getAsJsonObject();
	    } catch (JsonParseException ex) {
	        try {
	        	//incase array is valid too
	        	JsonArray o = new JsonParser().parse(test).getAsJsonArray();
	        } catch (JsonParseException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}
