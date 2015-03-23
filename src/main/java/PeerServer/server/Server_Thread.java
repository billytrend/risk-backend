/**
 * 
 */
package PeerServer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author 12001995
 *
 */
public class Server_Thread implements Runnable {

	private Socket connectionSocket;
	private Thread thread;
	private int connectionNumber;
	private PrintWriter toClient;
	private BufferedReader fromClient;

	public Server_Thread(Socket clientSocket, int clientNumber) throws IOException {
		this.connectionSocket = clientSocket;
		this.connectionNumber = clientNumber;
		
		//toClient = new PrintWriter(connectionSocket.getOutputStream());
		//toClient.println("FUCKING YOLO");
		
		//Set up I/O and launch new connection thread
		if (setOutputStream() && setInputStream()) {
			thread = new Thread(this);
			thread.start();
		} else {
			System.out.println("Problem with the Input/Output Streams. ");
			System.out
			.println("The Thread was not created for the following Connection: "
					+ connectionNumber);
		}
	}

	/**
	 * Checks whether the Input Stream for the connectionSocket has been set up.
	 * 
	 * @return true if a buffering character-input stream has been created,
	 *         false if otherwise
	 * @throws IOException
	 */
	public boolean setInputStream() {
		try {
			fromClient = new BufferedReader(new InputStreamReader(
					connectionSocket.getInputStream()));
			return true;
		} catch (IOException e) {
			System.err
			.println("IOException: problem creating the input stream. The socket is closed/ The socket is not connected/ The socket input has been shutdown using");
			return false;
		}
	}

	/**
	 * Checks whether the Output Stream for the connectionSocket has been set
	 * up.
	 * 
	 * @return true if created a new print writer to write data to the socket,
	 *         false otherwise.
	 * @throws IOException
	 */
	public boolean setOutputStream() {
		try {
			toClient = new PrintWriter(connectionSocket.getOutputStream());
			return true;
		} catch (IOException e) {
			System.err
			.println("IOException: problem creating the output stream/ The socket is not connected");
			return false;
		}
	}

	@Override
	public void run() {
		//Output to user the number of the current connection
		System.out.println("Conection : " + connectionNumber);
		toClient.println("Hello your are connected to RISK ");
		System.out.println("SWAG");
		String message = "";
		try {
			while((message = fromClient.readLine()) != null){
				System.out.println("Incoming client message: " + message);
			}
			toClient.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		

	}
	
	//check the string passed in is actually JSON 
	public boolean isJSONValid(String test) {
	    try {
	        new JsonObject();
	    } catch (JsonParseException ex) {
	        //check it is not JSON array
	        try {
	            new JsonArray();
	        } catch (JsonParseException ex1) {
	            return false;
	        }
	    }
	    return true;
	}

}
