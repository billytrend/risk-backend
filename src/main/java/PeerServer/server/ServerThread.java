/**
 * 
 */
package PeerServer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author 120011995
 * @category Class to implement multithreading in TCP PeerServer
 */
public class ServerThread extends Thread {
	public ServerSocket client;
	
	ServerThread(ServerSocket client){
		this.client = client;
	}
	
	public void run(){
		Socket socket = null;
		try {
			socket = client.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(true){
			try {
				String message = "";
				BufferedReader reader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
				while((message = reader.readLine()) != null){
					System.out.println("Incoming client message: " + message);
				}
				socket.close();	
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
