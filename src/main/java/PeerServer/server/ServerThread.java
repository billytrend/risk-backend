/**
 * 
 */
package PeerServer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author 120011995
 * @category Class to implement multithreading in TCP 
 * 			 PeerServer
 */
public class ServerThread extends Thread {
	Socket socket;
	ServerThread(Socket socket){
		this.socket = socket;
	}

	public void run(){
		try {
			String message = "";
			BufferedReader reader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			while((message = reader.readLine()) != null){
				System.out.println("Incoming client message: " + message);
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
