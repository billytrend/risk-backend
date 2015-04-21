package PeerServer.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PeerConnection implements Runnable {

	private DataInputStream inFromClient; 
    private DataOutputStream outToClient;
	private Socket socket;
	private BlockingQueue<String> commandQueue;
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
		
	public PeerConnection(Socket newSocket, int id2,
			BlockingQueue<String> commandQueue) {
		
		this(newSocket, id2);
		this.commandQueue = commandQueue;
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
		} catch (Exception e) {
		}
		
		try {
			if(response != ""){
				commandQueue.put(response);
				System.out.println("GOT: ----- " + response);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	
	@Override
	public void run() {
		while(true){
			receiveCommand();
		}
	}


}
