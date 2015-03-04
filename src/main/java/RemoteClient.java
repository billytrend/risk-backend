import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.net.SocketFactory;

/**
 * 
 */

/**
 * @author 120011995
 *
 */
public class RemoteClient implements Runnable {

	private int portNumber = 4444;
	private String hostName = "localhost";
	private Socket connectionSocket;
	private BufferedReader fromServer;
	private PrintWriter toServer;
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteClient rm = new RemoteClient();
		new Thread(rm).start();
	}



	@Override
	public void run() {
		
		try {
			connectionSocket = SocketFactory.getDefault().createSocket(
					hostName, portNumber);
			toServer = new PrintWriter(connectionSocket.getOutputStream());	
			fromServer = new BufferedReader(new InputStreamReader(
					connectionSocket.getInputStream()));
			
			String messageToServer = null;
			String messageFromServer = null;
			
			//while (connectionSocket.isConnected()) {
				System.out.println("YO CLIENTS");
				messageToServer = "hey im a client";
				toServer.println(messageToServer);
			//}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
