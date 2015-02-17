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
 * @category Class to implement multi-threading in TCP PeerServer
 */
public class ServerThread implements Runnable {
	public Socket client;
	
	
	ServerThread(Socket client){
		this.client = client;
	}

	@Override
	public void run() {
		
		while(true){
			try {
				
				String message = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				while((message = reader.readLine()) != null){
					System.out.println("Incoming client message: " + message);
				}
				
				client.close();	
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

//	public void run(){
//		
//		while(true){
//			try {
//				
//				String message = "";
//				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
//				
//				while((message = reader.readLine()) != null){
//					System.out.println("Incoming client message: " + message);
//				}
//				client.close();	
//			} 
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
}
