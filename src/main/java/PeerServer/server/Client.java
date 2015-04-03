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

public class Client{
	//socket conencted to server
	private Socket socket;
	//streams used to communicate to server come from socket
	private DataOutputStream out;
	private DataInputStream in;
	private final static int port = 4444;

	public static void main(String[] args) {
		String clientName = args[0];
		new Client("localhost", port); 
	}

	/**
	 * Constructor
	 * @param host
	 * @param port
	 */
	public Client(String host, int port){
		try {
			socket = new Socket(host, port);
			System.out.println("connected to " + socket);
			
			//Add the sockets streams to data sockets
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends message to server
	 * @param message
	 */
	public void send(String message) {
		try {
			// Send it to the server
			out.writeUTF(message);
		} catch( IOException ie ) { 
			System.out.println( ie ); 
		}
	}
	
	
	public String receive(){
		String message = "";
		try {
			message = in.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

