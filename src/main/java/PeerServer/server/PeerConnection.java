package PeerServer.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PeerConnection {

	private DataInputStream inFromClient; 
    private DataOutputStream outToClient;
	private Socket socket;
	private int id;
	
    
	public PeerConnection(Socket socket, int id){
		this.socket = socket;
		this.id = id;
		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	public void sendCommand(String command){
		try {
			outToClient.writeUTF(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String receiveCommand(){
		String response = "";
		
		try {
			response = inFromClient.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

}
