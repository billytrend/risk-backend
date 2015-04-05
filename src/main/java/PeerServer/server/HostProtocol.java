package PeerServer.server;

import static com.esotericsoftware.minlog.Log.debug;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import javax.management.monitor.CounterMonitor;
import javax.validation.Payload;

import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;

import PeerServer.protocol.setup.*;
import PeerServer.protocol.general.*;
import PeerServer.protocol.dice.*;
import PeerServer.protocol.cards.*;
import PeerServer.protocol.gameplay.*;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


/**
 * An instance of this class represents a game that
 * is currently being played over the network.
 * 
 */
public class HostProtocol extends AbstractProtocol {
	
	private int maxNoOfPlayers = 6;		//min is 3	
	private int minNoOfPlayers = 2;
	private int waitTimeInSeconds = 3; // host waits 180 seconds for remaining players to connect

	// mappings of features / versions to the count of how many clients support these
	private HashMap<String, Integer> supportedFeatures = new HashMap<String, Integer>();
	private HashMap<Integer, Integer> supportedVersions = new HashMap<Integer, Integer>();
	private ArrayList<PeerConnection> connections = new ArrayList<PeerConnection>(); 	// players connections
	
	// maps players IDs with a connection associated with them (null for local)
	private HashMap<Integer, PeerConnection> connectionMapping = new HashMap<Integer, PeerConnection>();
	
	// connection which is currently served
	private PeerConnection currentConnection;
	private Set<PeerConnection> acknowledgements = new HashSet<PeerConnection>(); // IDs
	private Socket newSocket;
	private String newName = "";
	private Timer timer = new Timer();
	private Thread mainThread = Thread.currentThread();
	
	// only commands host received over network - not sends
	@Override
	protected void handleSetupCommand(String command){
		switch(this.protocolState){
			case JOIN_GAME:
				debug("\nJOIN_GAME");
				this.protocolState = join_game(command);			
				break;		
			case PING:
				debug("\n PING");
				this.protocolState = ping(command);
				break;
			case ACK:
				debug("\n PING");
				this.protocolState = acknowledge(command);
				break;
			/*case PING_ACK:
				debug("\n PING_ACK");
				this.protocolState = ping_ack(command);
				break;
			case READY:
				debug("\n READY");
				this.protocolState = ready(command);
				break;
			case ACK:
				debug("\n ACK");
				this.protocolState = receive_ack(command);
				break;
			/*case INIT_GAME:
				debug("\n INIT_GAME");
				this.protocolState = init_game(command);
				break;
			case SETUP_GAME:
				debug("\n SETUP_GAME");
				this.protocolState = setup_game(command);
				break;*/
			default:
				System.out.println("IN DEFAULT not good");
				break;
		}
	}
	
	
	@Override
	protected ProtocolState join_game(String command) {
		if(Thread.interrupted()){
			
		}
		
		System.out.println("\nGOT: " + command);
		
		//create JSON object of command
		join_game join_game = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);
		if(join_game == null){
			errorMessage = ("Wrong request");
			return reject_join_game("");
		}
		
		if(startingPlayers.size() >= maxNoOfPlayers){
			errorMessage = ("Full");
			return reject_join_game("");
		}
		
		// game will be accepted so update versions and features counts
		update(join_game.payload.supported_features, supportedFeatures);
		update(join_game.payload.supported_versions, supportedVersions);
		newName = join_game.payload.name;
		
		if(startingPlayers.size() + 1 == minNoOfPlayers){// state is going to change to ready after that time{
			timer.schedule(new ChangeState(), waitTimeInSeconds * 1000);
		}
		
		return accept_join_game(command);
	}
	



	@Override
	protected ProtocolState accept_join_game(String command) {		
		// new id generated
		int id = startingPlayers.size();
		
		// new connection is created for a new accepted player
		PeerConnection newConnection = new PeerConnection(newSocket, id);
		connections.add(newConnection);
		connectionMapping.put(id, newConnection);
		currentConnection = newConnection;
		
		// TODO: starting armies fix
		// TODO: add logic between host being a player
		
		// creating player and mapping its id to its interface
		PlayerInterface playerInterface = new RemotePlayer();
		if(newName != "")
			startingPlayers.add(new Player(playerInterface, 0, id, newName));
		else
			startingPlayers.add(new Player(playerInterface, 0, id));
		
		interfaceMapping.put(0, playerInterface);
		state.setPlayers(startingPlayers);
		
		
		//sending response - about being accepted
		accept_join_game accept_join_game =
				new accept_join_game(id, ack_timeout, move_timeout);
		
		newConnection.sendCommand(Jsonify.getObjectAsJsonString(accept_join_game));
		
		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(accept_join_game));
		
		return players_joined("");
	}

	
	@Override
	protected ProtocolState reject_join_game(String reason) {
		//create json string 
		reject_join_game reject_join_game = new reject_join_game(errorMessage);		
		String rj = Jsonify.getObjectAsJsonString(reject_join_game);
		
		
		DataOutputStream out;
		try {
			out = new DataOutputStream(newSocket.getOutputStream());
			//send rejection 
			out.writeUTF(rj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\nSEND: " + rj);

		return protocolState;
	}


	@Override
	protected ProtocolState players_joined(String command){
		players_joined players_joined = new players_joined(startingPlayers);
		String npj = Jsonify.getObjectAsJsonString(players_joined);
		
		System.out.println("\nSEND: " + npj);
		currentConnection.sendCommand(npj);
		
		// sending only new player to the rest
		int id = currentConnection.getId();
		
		players_joined toRest = new players_joined(new String[]{Integer.toString(id), newName, "key"});
		sendToAllExcept(Jsonify.getObjectAsJsonString(toRest), currentConnection);		
		
		System.out.println("\nSEND TO ALMOST ALL: " + Jsonify.getObjectAsJsonString(toRest));
	
		return protocolState;
	}


	@Override
	protected ProtocolState ping(String command){
		if(Thread.interrupted()){
			synchronized(this){
				timer.notify();
				try {
					timer.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// host is pinging
		if(command == ""){
			// add all players to the state - will be deleted later if needed
			state.setPlayers(startingPlayers);
				
			ping ping = new ping(startingPlayers.size(), 0);
			String p = Jsonify.getObjectAsJsonString(ping);
			//send ping to each client
			sendToAll(p);
			System.out.println("\nSEND TO ALL: " + p);
			
			timer.schedule(new ChangeState(), ack_timeout * 1000);
		}
		else{
			ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);
			if(ping == null){
				leaveCode = 200;
				leaveReason = "Wrong command: expected ping";
				return ProtocolState.LEAVE_GAME;
			}
			System.out.println("\nGOT: " + command);
			
			// forward ping to everyone
			sendToAllExcept(command, currentConnection);
			System.out.println("\nSEND TO AMOST ALL: " + command);
			
			acknowledgements.add(currentConnection);
		}
		
		return ProtocolState.PING;
	}
	
	
	@Override
	protected ProtocolState ready(String command){
		// check for any dropouts 
		for(PeerConnection c : connections){
			if(!acknowledgements.contains(c)){
				// removing players that have not sent pong
				timeout timeout = new timeout(0, 1, c.getId());
				removePlayer(c);
			}
		}
		
		// if less than 3 players remained send leave
		if(startingPlayers.size() < minNoOfPlayers)
			return leave_game("");
		
		//send ready command to all connected clients ACK required
		ready ready = new ready(null, 0, 1);
		sendToAll(Jsonify.getObjectAsJsonString(ready));
		System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(ready));
		
		timer.schedule(new ChangeState(), ack_timeout * 1000);
		acknowledgements.clear();
		
		return ProtocolState.ACK;
	}


	/**
	 * Receives acknowledgements (either ping or acknowledgement)
	 * it keeps receiving these until the timeout passes.
	 * 
	 * @param command
	 * @param isPing
	 * @return
	 */
	private ProtocolState acknowledge(String command) {
		if(Thread.interrupted()){
			synchronized(this){
				timer.notify();
				try {
					timer.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		acknowledgement ack = (acknowledgement) Jsonify.getJsonStringAsObject(command, acknowledgement.class);
		if(ack == null){
			leaveCode = 200;
			leaveReason = "Wrong command: expected acknowledge";
			return ProtocolState.LEAVE_GAME;
		}
		System.out.println("\nGOT: " + command);
		
		acknowledgements.add(currentConnection);
		return ProtocolState.ACK;
	}
	
	
	@Override
	protected ProtocolState timeout(String command){
		for(PeerConnection c : connections){
			if(!acknowledgements.contains(c)){
				// removing players that have not sent acknowledgement
				// message about timeout needs to be sent to all players
				timeout timeout = new timeout(0, 1, c.getId());
				removePlayer(c);
				
				System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(timeout));
				sendToAll(Jsonify.getObjectAsJsonString(timeout));
			}
		}
		
		if(startingPlayers.size() < minNoOfPlayers){
			return leave_game("");
		}
		
		return init_game("");
	}
	

	@Override
	protected ProtocolState init_game(String command){
		// finding the highest version supported by all
		int version = findHighestVersionSupportedByAll(supportedVersions);
		
		// get features that are supported by all
		ArrayList<String> features = findFeaturesSupportedByAll(supportedFeatures);
		String[] feat = new String[features.size()];
		features.toArray(feat);
		
		initialise_game init_game = new initialise_game(version, feat);
		sendToAll(Jsonify.getObjectAsJsonString(init_game));
		
		System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(init_game));
		return leave_game(""); // TODO: JUST FOR NOW - TO STOP - CHANGE IT LATER.
	}


	@Override
	protected ProtocolState leave_game(String command) {
		// if command is not empty sb sent
		if(command != ""){
			leave_game leave = (leave_game) Jsonify.getJsonStringAsObject(command, leave_game.class);
			if(leave == null){
				leaveCode = 200;
				leaveReason = "Wrong command";
				return leave_game("");
			}
			
			int responseCode = leave.payload.response;
			String message = leave.payload.message;
			
			sendToAllExcept(command, currentConnection);
			System.out.println("\nSEND ALMOST ALL: " + Jsonify.getObjectAsJsonString(command));
			return protocolState;
		}
		// if command is empty it is us that want to leave
		else{
			leave_game leave = new leave_game(leaveCode, leaveReason, false, 0);
			
			System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(leave));
			sendToAll(Jsonify.getObjectAsJsonString(leave));
			return ProtocolState.LEAVE_GAME;
		}
	}
	
	
	@Override
	protected ProtocolState setup_game(String command){
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.setup.class);
		return protocolState;	
	}

	
	
	/**
	 * Removes the given connection from the list of supported connections
	 * and a player associated with it from the game state
	 * @param connection
	 */
	protected void removePlayer(PeerConnection connection){
		int ID = -1;
		for(int id : connectionMapping.keySet()){
			if(connectionMapping.get(id) == connection)
				ID = id;
		}
		
		// remove that player from connections
		connections.remove(connection);
		
		if(ID != -1){
			connectionMapping.remove(ID);
			// remove that player from the game state
			removePlayer(ID);
		}
	}
	
	
	/**
	 * Sends a given command to all supported connections (all clients)
	 * @param command
	 */
	private void sendToAll(String command){
		for (PeerConnection c : connections) {
			c.sendCommand(command);
		}
	}

	private void sendToAllExcept(String command, PeerConnection con) {
		for (PeerConnection c : connections) {
			if(c != con)
				c.sendCommand(command);
		}
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	private ArrayList<String> findFeaturesSupportedByAll(
			HashMap<String, Integer> map) {
		ArrayList<String> features = new ArrayList<String>();
		
		for(String a : map.keySet()){
			if(map.get(a) == connections.size())
				features.add(a);
		}	
		return features;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	private int findHighestVersionSupportedByAll(HashMap<Integer, Integer> map){
		int supported = 1;
	    
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()){
			Integer current = it.next();
			if(map.get(current) == connections.size())
				supported = current;
		}
		
		return supported;
	}

	/**
	 * Updates a given mapping based on the input array.
	 * Increases count of features / versions specified in the array.
	 * 
	 * @param input
	 * @param toUpdate
	 */
	private void update(Object[] input,	HashMap toUpdate) {
		Arrays.sort(input);
		Integer count;
		for(Object a : input){
			if(toUpdate.containsKey(a)){
				count = (Integer) toUpdate.get(a);
				toUpdate.put(a, count + 1);
			}
			else{
				toUpdate.put(a, 1);
			}
		}
	}

	public static void main(String[] args) {
		HostProtocol protocol = new HostProtocol();
		
		protocol.run();
	}

	public void run() {
		state = new State();
		RiskMapGameBuilder.addRiskTerritoriesToState(state);
		
		// creating our player
		localPlayer = new DumbBotInterface();
		startingPlayers.add(new Player(localPlayer, 0, 0, getRandomName()));
		connectionMapping.put(0, null);
		
		try {
			// socket used to accept all incoming connections (at the start)
			ServerSocket socket = new ServerSocket(4444);
			socket.setSoTimeout(10000);
			DataInputStream inFromClient; 
			
			// accepting new connections
			while(protocolState == ProtocolState.JOIN_GAME){
				try{
					newSocket = socket.accept();
					newSocket.setSoTimeout(10000);
					inFromClient = new DataInputStream(newSocket.getInputStream());
					handleSetupCommand(inFromClient.readUTF()); 
				} catch (Exception e){
					break;
				}
			}
			
			// everyone has joined, start handling their commands
			while(protocolState != ProtocolState.LEAVE_GAME){
				if(Thread.interrupted()){
					synchronized(timer){
						try {
							timer.wait();
						} catch (Exception e) {
						}
					}
					
				}
				for (PeerConnection c : connections) {
					if(protocolState == ProtocolState.LEAVE_GAME)
						break;
					String playersLatestCommand = c.receiveCommand();
					if (!playersLatestCommand.equals("")) {
						currentConnection = c;
						handleSetupCommand(playersLatestCommand); //TODO: change later for non setup
					}		
				}
				
			}
			
			// send command to everyone about leaving game
			leave_game("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	class ChangeState extends TimerTask{
		@Override
		public void run() {
			if(protocolState == ProtocolState.JOIN_GAME){
				protocolState = ping("");
			}
			else if(protocolState == ProtocolState.PING){
				mainThread.interrupt();
				synchronized(this){
					try {
						wait(10000);
					} catch (Exception e) {
					}
					
					protocolState = ready("");
					notify();
				}
			}
			else if(protocolState == ProtocolState.ACK){
				mainThread.interrupt();
				synchronized(this){
					try {
						wait(10000);
					} catch (InterruptedException e) {
					}
		
					protocolState = timeout(""); 
					notify();
				}
			}
		}		
	}


}