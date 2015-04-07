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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

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
import PlayerInput.RemotePlayer;
import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;

import com.esotericsoftware.minlog.Log;
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
	private ServerSocket serverSocket;
	private String newName = "";
	private String newKey = "";
	private Timer timer = new Timer();
	private ChangeState currentTask;
	private Thread mainThread = Thread.currentThread();
	
	
	@Override
	protected void takeSetupAction() {
		//Log.DEBUG = true;
		ArrayList<String> allCommands;
		// check if timer didn't interrupt
		if(Thread.interrupted()){
			synchronized(getCurrentTask()){
				try {
					// if timer wants to change the current state, don't do anything
					// let it change it to avoid concurrency problems
					//timer.wait();
					currentTask.wait();
				} catch (Exception e) {}
			}
		}
		
		switch(protocolState){
			case JOIN_GAME:
				debug("\nJOIN_GAME");
				// accept a new client and handle their request
				try {
					newSocket = serverSocket.accept();
					newSocket.setSoTimeout(1000);
					DataInputStream fromClient = new DataInputStream(newSocket.getInputStream());
					this.protocolState = join_game(fromClient.readUTF());
				} catch (IOException e) {}
				break;		
			case ACCEPT_JOIN_GAME:
				debug("\n ACCEPT_JOIN_GAME");
				protocolState = accept_join_game("");
				break;
			case PLAYERS_JOINED:
				debug("\n PLAYERS_JOINED");
				protocolState = players_joined("");
	
				// if we got min number of players, schedule new task for timer
				// after a specified time it will transfer the state to the next one (PING)
				if(startingPlayers.size() == minNoOfPlayers){
					currentTask = new ChangeState();
					timer.schedule(currentTask, waitTimeInSeconds * 1000);
				}
				break;
			case REJECT_JOIN_GAME:
				debug("\n REJECT_JOIN_GAME");
				protocolState = reject_join_game("");
				break;
			case PING:
				debug("\n PING");
				protocolState = ping("");
				currentTask = new ChangeState();
				timer.schedule(currentTask, ack_timeout * 1000);
				break;
			case PING_ACK:
				debug("\n PING_ACK");
				// get command from each connection (if there are any commands)
				allCommands = getAllPlayersCommands();
				for(String command : allCommands){
					ping(command);
				}
				break;
			case READY:
				debug("\n READY");
				this.protocolState = ready("");
				currentTask = new ChangeState();
				timer.schedule(currentTask, ack_timeout * 1000);
				break;
			case ACK:
				debug("\n ACKNOWLEDGE");
				allCommands = getAllPlayersCommands();
				for(String command : allCommands){
					acknowledge(command);
				}
				break;
			case INIT_GAME:
				debug("\n INIT_GAME");
				protocolState = init_game("");
				break;
			case SETUP_GAME:
				debug("\n SETUP_GAME");
				this.protocolState = setup_game("");
				break;
			case LEAVE_GAME:
				debug("\n LEAVE_GAME");
				this.protocolState = leave_game("");
				break;
			default:
				break;
		}
	}

	
	private Object getCurrentTask() {
		return currentTask;
	}


	@Override
	protected ProtocolState join_game(String command) {	
		System.out.println("\nGOT: " + command);
		
		//create JSON object of command
		join_game join_game = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);
		if(join_game == null){
			errorMessage = ("Wrong request");
			return ProtocolState.REJECT_JOIN_GAME;
		}
		
		if(startingPlayers.size() >= maxNoOfPlayers){
			errorMessage = ("Full");
			return ProtocolState.REJECT_JOIN_GAME;
		}
		
		// game will be accepted so update versions and features counts
		update(join_game.payload.supported_features, supportedFeatures);
		update(join_game.payload.supported_versions, supportedVersions);
		newName = join_game.payload.name;
		newKey = join_game.payload.public_key;
		return ProtocolState.ACCEPT_JOIN_GAME;
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
		BlockingQueue<Object> newSharedQueue = new LinkedBlockingQueue<Object>();
		queueMapping.put(id, newSharedQueue);
		PlayerInterface playerInterface = new RemotePlayer(newSharedQueue);
		Player newOne;
		if(newName != "")
			newOne = new Player(playerInterface, 0, id, newName);
		else
			newOne = new Player(playerInterface, 0, id);
		
		newOne.setPublicKey(newKey);
		startingPlayers.add(newOne);
		//interfaceMapping.put(0, playerInterface);
		state.setPlayers(startingPlayers);
		
		//sending response - about being accepted
		accept_join_game accept_join_game =
				new accept_join_game(id, ack_timeout, move_timeout);
		
		newConnection.sendCommand(Jsonify.getObjectAsJsonString(accept_join_game));
		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(accept_join_game));
		
		return ProtocolState.PLAYERS_JOINED;
	}

	
	@Override
	protected ProtocolState reject_join_game(String reason) {
		//create json string 
		reject_join_game reject_join_game = new reject_join_game(errorMessage);		
		String rj = Jsonify.getObjectAsJsonString(reject_join_game);
		
		// dont create peer connection for this client, they are rejected
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

		return protocolState.JOIN_GAME;
	}


	@Override
	protected ProtocolState players_joined(String command){
		players_joined players_joined = new players_joined(startingPlayers);
		String npj = Jsonify.getObjectAsJsonString(players_joined);
		
		System.out.println("\nSEND: " + npj);
		currentConnection.sendCommand(npj);
		
		// sending only new player to the rest
		int id = currentConnection.getId();
		
		players_joined toRest = new players_joined(new String[]{Integer.toString(id), newName, newKey});
		sendToAllExcept(Jsonify.getObjectAsJsonString(toRest), currentConnection);		
		
		System.out.println("\nSEND TO ALMOST ALL: " + Jsonify.getObjectAsJsonString(toRest));
	
		return protocolState.JOIN_GAME;
	}


	@Override
	protected ProtocolState ping(String command){
		// host is pinging
		if(command == ""){
			// add all players to the state - will be deleted later if needed
			state.setPlayers(startingPlayers);
			
			// create and send a ping to all clients
			ping ping = new ping(startingPlayers.size(), 0);
			String p = Jsonify.getObjectAsJsonString(ping);
			sendToAll(p);
			System.out.println("\nSEND TO ALL: " + p);
			
			return ProtocolState.PING_ACK;
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
		
		return ProtocolState.PING_ACK;
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
		if(startingPlayers.size() < minNoOfPlayers){
			return ProtocolState.LEAVE_GAME;
		}
		
		//send ready command to all connected clients ACK required
		ready ready = new ready(null, 0, ack_id);
		ack_id++; // TODO: I guess its supposed to work like this
		sendToAll(Jsonify.getObjectAsJsonString(ready));
		System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(ready));
		
		// clear all ping acknowledgements
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
	protected ProtocolState acknowledge(String command) {
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
		return ProtocolState.LEAVE_GAME; // TODO: JUST FOR NOW - TO STOP - CHANGE IT LATER.
	}


	@Override
	protected ProtocolState leave_game(String command) {
		// if command is not empty sb sent a leave_command
		if(command != ""){
			leave_game leave = (leave_game) Jsonify.getJsonStringAsObject(command, leave_game.class);
			if(leave == null){
				leaveCode = 200;
				leaveReason = "Wrong command";
				return leave_game("");
			}
			
			//TODO: print a reason?
			int responseCode = leave.payload.response;
			String message = leave.payload.message;
			
			sendToAllExcept(command, currentConnection);
			System.out.println("\nSEND ALMOST ALL: " + Jsonify.getObjectAsJsonString(command));
			// dont change state
			return protocolState;
		}
		// if command is empty it is us that want to leave
		else{
			leave_game leave = new leave_game(leaveCode, leaveReason, false, 0);
			
			System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(leave));
			sendToAll(Jsonify.getObjectAsJsonString(leave));
			
			// wait a bit for everyone to receive hosts leaving message
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	@Override
	protected ProtocolState setup_game(String command){
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.gameplay.setup.class);
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

	
	/**
	 * Loops over all connections (one for each player), receive
	 * their latest commands and return them all in a list
	 * @return
	 */
	private ArrayList<String> getAllPlayersCommands(){
		ArrayList<String> commands = new ArrayList<String>();
		
		for (PeerConnection c : connections) {
			if(protocolState == ProtocolState.LEAVE_GAME)
				break;
			String playersLatestCommand = c.receiveCommand();
			if (!playersLatestCommand.equals("")) {
				currentConnection = c;
				commands.add(playersLatestCommand);
			}		
		}
		return commands;
	}
	


	@Override
	public void run(){
		// creating our player
		localPlayer = new DumbBotInterface();
		startingPlayers.add(new Player(localPlayer, 0, 0, getRandomName()));
		connectionMapping.put(0, null);
		
		try {
			serverSocket = new ServerSocket(4444);
			serverSocket.setSoTimeout(2000); // socket waits for 2 seconds -- needed for checking interrupts
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.run();
		
		timer.cancel();
		
		// closing all sockets
		for(PeerConnection con : connections){
			con.close();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ChangeState represents a thread that is supposed
	 * to run after a time specified in a timer which uses it
	 * The thread simply changes the state of the game to the next one
	 *
	 */
	class ChangeState extends TimerTask{
		@Override
		public void run() {
			ProtocolState newState = protocolState;
			
			// interrupt the main thread - for safety, so it doesn't access the protocolState
			// field we want to change and to make sure its not in the middle of sth that
			// we can mess up by changing state
			mainThread.interrupt();
			synchronized(this){
				try {
					// wait for a bit second to let the main thread reach interrupt
					wait(3000);
				} catch (Exception e) {}
				
				if(protocolState == ProtocolState.JOIN_GAME)
					protocolState = ProtocolState.PING;
				else if(protocolState == ProtocolState.PING_ACK)
					protocolState = ProtocolState.READY;
				else if(protocolState == ProtocolState.ACK)
					protocolState = ProtocolState.INIT_GAME;
				
				// change state and notify the main thread so it continues running
				notify();
			}
		}		
	}





}