
package PeerServer.server;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.leave_game;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.esotericsoftware.minlog.Log.debug;


/**
 * An instance of this class represents a game that
 * is currently being played over the network.
 * 
 */
public class HostProtocol extends AbstractProtocol  {

	private int maxNoOfPlayers = 6;
	private int minNoOfPlayers = 2;
	private int waitTimeInSeconds = 3; // host waits 3 seconds for the rest of the players

	// mappings of features / versions to the count of how many clients support these
	private HashMap<String, Integer> supportedFeatures = new HashMap<String, Integer>();
	private HashMap<Integer, Integer> supportedVersions = new HashMap<Integer, Integer>();
	
	// maps players IDs with a connection associated with them
	private HashMap<Integer, PeerConnection> connectionMapping = new HashMap<Integer, PeerConnection>();

	// connection which is currently served
	private PeerConnection currentConnection;
	private Set<Integer> acknowledgements = new HashSet<Integer>(); 
	private Socket newSocket; 
	private ServerSocket serverSocket;

	
    public HostProtocol(State state) {
        this.state = state;
    }

	@Override
	/**
	 * Starts the handshake. Accepts all players and prepared to play the game.
	 */
	protected void takeSetupAction() {
		ArrayList<String> allCommands;
		
		// timer can interrupt the game to change the  state
		// after acknowledgement timeout
		if(Thread.interrupted()){
			synchronized(currentTask){
				try {
					currentTask.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		switch(protocolState){
		case JOIN_GAME:
			debug("\nJOIN_GAME");
			System.out.println("join game");
			// accept a new client and handle their request
			try {
				newSocket = serverSocket.accept();
				newSocket.setSoTimeout(1000);
				DataInputStream fromClient = new DataInputStream(newSocket.getInputStream());
				
				// handle the command and either accept and create player or send them reject msg
				handleJoinGameCommand(fromClient.readUTF());
			} catch (IOException e) {}
			
			// no need to transfer to the other state - this will happen 
			// automatically once we receive enough players and the timeout has passed
			
			break;		
		case PING:
			System.out.println("ping");
			debug("\n PING");
			sendPing();  // the game will be transfered to the next state
							// after ack timeout has passed
			break;
		case READY:
			debug("\n READY");
			sendReady();      // state will be changed after all ack are handled
			
			break;
		case INIT_GAME:
			debug("\n INIT_GAME");
			sendInitializeGame();
			//sendLeaveGame(200, "temporary leave");
			protocolState = ProtocolState.START_GAME;
			break;
		default:
			break;
		}
	}


// =====================================================================
// HANDLING VARIOUS COMMANDS
// =====================================================================

// ************************
// JOINGING GAME
// ************************
	
	/**
	 * Used when the server receives request to join the game
	 * It handles everything - sends accept command or reject command
	 * and adds player if they are accepted. I also sends players_joined
	 * command to the accepted player
	 * 
	 * @param command
	 * @return
	 */
	protected void handleJoinGameCommand(String command) {	
		System.out.println("\nGOT: " + command + "\n");

		join_game join_game = null;
		try{
		//create JSON object of command
			join_game = (join_game) Jsonify.getObjectFromCommand(command, join_game.class);
		} catch (ClassCastException e){
			errorMessage = ("Wrong join game request");
			sendRejectJoin(); // reject player
		}

		
		// the game is full, reject a player
		if(numOfPlayers == maxNoOfPlayers){
			sendRejectJoin();
		}
		
		// game will be accepted so update versions and features counts
		update(join_game.payload.supported_features, supportedFeatures);
		update(join_game.payload.supported_versions, supportedVersions);
		String newName = join_game.payload.name;
		String newKey =  join_game.payload.public_key;
		
		// accpet a player
		sendAcceptJoin(newName, newKey);
		sendPlayersJoined(newName, newKey); // send them the information about joined players;
	}

	/**
	 * Sent to the player if they are rejected
	 */
	protected void sendRejectJoin() {
		//create json string 
		reject_join_game reject_join_game = new reject_join_game(errorMessage);		
		String rj = Jsonify.getObjectAsCommand(reject_join_game);

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
	}

	/**
	 * Sent to the player if they are accepted - the method also creates a new
	 * player with the given name and a key
	 * 
	 * @param name
	 * @param key
	 */
	protected void sendAcceptJoin(String name, String key) {		
		// new id generated
		int id = startingPlayers.size();

		// new connection is created for a new accepted player
		PeerConnection newConnection = new PeerConnection(newSocket, id);
		connectionMapping.put(id, newConnection);
		currentConnection = newConnection;

		Player newOne = createNewPlayer(name, id, false);
		newOne.setPublicKey(key);

		//sending response - about being accepted
		accept_join_game accept_join_game =
				new accept_join_game(id, ack_timeout, move_timeout);
		
		sendToCurrentConnection(Jsonify.getObjectAsCommand(accept_join_game));
	}
	
	
	/**
	 * Information about all players in the game sent to the newly joined player
	 * and to other players to notify them about the newly joined player
	 * 
	 * @param newName
	 * @param newKey
	 */
	protected void sendPlayersJoined(String newName, String newKey){
		players_joined players_joined = new players_joined(startingPlayers);
		sendToCurrentConnection(Jsonify.getObjectAsCommand(players_joined));

		// sending only new player to the rest
		int id = currentConnection.getId();

		players_joined toRest = new players_joined(new Object[]{id, newName, newKey});
		sendCommand(Jsonify.getObjectAsCommand(toRest), id, false); // send to almost all and 
																		// don't acknowledge
		
		// if we already got enough players start the timer 
		// it will interrupt and change out state to PING after the specified time
		if(startingPlayers.size() == minNoOfPlayers){

			currentTask = new ChangeState(ProtocolState.PING); 
			timer.schedule(currentTask, waitTimeInSeconds * 1000);
		}
	}
	
// **********************************
// PINGING AND PRE-GAME PREPARATION
// **********************************
	
	/**
	 * Send a ping command to everyone and receive their pings.
	 * If someone does not respond with their pings, remove them.
	 * If there is not enough players left, finish the game.
	 */
	protected void sendPing(){
		// add all players to the state - will be deleted later if needed
		state.setPlayers(startingPlayers);
        RiskMapGameBuilder.colourPlayers(startingPlayers);

		// create and send a ping to all clients
		ping ping = new ping(startingPlayers.size(), 0);
		String p = Jsonify.getObjectAsCommand(ping);
		
		nextStateAfterAck = ProtocolState.READY;
		sendCommand(p, null, true); // send the command to all and expect pings (acknowledgements)
	}
	
	
	/** 
	 * Send ready command to everyone and receive their acknowledgements.
	 * 
	 */
	protected void sendReady(){
		//send ready command to all connected clients ACK required
		ready ready = new ready(null, myID, ack_id);
		ack_id++;
		
		// clear all previous acknowledgements
	
		nextStateAfterAck = ProtocolState.INIT_GAME;
		sendCommand(Jsonify.getObjectAsCommand(ready), null, true); // send to all
																		// and expect acknowledgements
	}
	
	/**
	 * Send initialise game to everyone. No acknowledgements required.
	 */
	protected void sendInitializeGame(){
		// finding the highest version supported by all
		int version = findHighestVersionSupportedByAll(supportedVersions);

		// get features that are supported by all
		ArrayList<String> features = findFeaturesSupportedByAll(supportedFeatures);
		String[] feat = new String[features.size()];
		features.toArray(feat);

		engine = new GameEngine(state, networkArbitration);
		
		initialise_game init_game = new initialise_game(version, feat);
		sendCommand(Jsonify.getObjectAsCommand(init_game), null, false); // send to all, no acks
	}

	
	/**
	 * Send leave game to everyone. Finishes the game.
	 * @return
	 */
	protected void sendLeaveGame(int leaveCode, String leaveReason) {
		leave_game leave = new leave_game(leaveCode, leaveReason, false, 0);

		sendCommand(Jsonify.getObjectAsCommand(leave), null, false);
		// wait a bit for everyone to receive hosts leaving message
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		protocolState = null;
	}
	
	
	/**
	 * Handles a leave game command received from someone. It removes the player
	 * from the game.
	 * @param command
	 */
	protected void handleLeaveGame(String command){
		// if command is not empty sb sent a leave_command
		leave_game leave = (leave_game) Jsonify.getObjectFromCommand(command, leave_game.class);

		//TODO: print a reason?
		int responseCode = leave.payload.response;
		String message = leave.payload.message;
		int id = leave.player_id;

		sendCommand(command, id, false); // send to all except the one leaving
											// no acknowledgements
		removePlayer(currentConnection);
		
		if(numOfPlayers < minNoOfPlayers){ // leave yourself if there is not enough players
			sendLeaveGame(200, "Not enough players");              // TODO: replace the guy with AI !
		}
	}
	
	
// =====================================================================
// HELPER FUNCTIONS
// =====================================================================

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

		if(ID != -1){
			connectionMapping.remove(ID);
			// remove that player from the game state
			removePlayer(ID);
		}
	}

	/**
	 * Returns an array of features supported by all players
	 * @param map
	 * @return
	 */
	private ArrayList<String> findFeaturesSupportedByAll(
			HashMap<String, Integer> map) {
		ArrayList<String> features = new ArrayList<String>();

		for(String a : map.keySet()){
			if(map.get(a) == connectionMapping.size())
				features.add(a);
		}	
		return features;
	}

	/**
	 * It removes the highest version supported by all players
	 * @param map
	 * @return
	 */
	private int findHighestVersionSupportedByAll(HashMap<Integer, Integer> map){
		int supported = 1;

		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()){
			Integer current = it.next();
			if(map.get(current) == connectionMapping.size())
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


// =====================================================================
// SENDING AND RECEIVING COMMANDS AND ACKNOWLEDGEMENTS
// =====================================================================
	
	
	
	/**
	 * Receives acknowledgements (either ping or acknowledgement)
	 * it keeps receiving these until the timeout passes. If the host
	 * receives a message that is neither an acknowledgement nor a ping
	 * it puts it to the queue of commands and these will be retrieved later
	 * 
	 * @param command
	 * @return
	 */
	protected void acknowledge(String command) {
		int id = -1;
		
		if(command.contains("ping")){
			ping ping = null;
			try{
				ping = (ping) Jsonify.getObjectFromCommand(command, ping.class);
			} catch (ClassCastException e){
				sendLeaveGame(200, "Wrong command: expected ping");
				return;
			}

			sendCommand(command, ping.player_id, false);
			id = ping.player_id;
		}
		
		
		else if(command.contains("leave")){ // TODO: think about this.
			handleLeaveGame(command); 
			return;
		}
		
		
		else if(command.contains("acknowledgement")){
			acknowledgement ack = null;
			try{
				ack = (acknowledgement) Jsonify.getObjectFromCommand(command, acknowledgement.class);
			} catch (ClassCastException e){
				sendLeaveGame(200, "Wrong command: expected acknowledge");
				return;
			}
			
			if((ack.payload != ack_id - 1) && (ack.payload != their_ack_id)){
				//System.out.println("Wrong acknoledgement - but we ignore it");
			}
			id = ack.player_id;
		}
		else{
			
			// it wasnt an acknowledgement but may be important later
			// add this to the command queue
			System.out.println("got something while acknowledging: " + command + "\n");
			if(command != "")
				commandQueue.add(command);
			return;
		}
		
		// add the acknowledging player id to the array of acknowledgements
		if(id != -1){
			acknowledgements.add(id);
		}
	}


	
	@Override
	/**
	 * Is sends commands to either all players or all players except
	 * the one with the given exceptId. Depending on whether the acknowledge
	 * boolean is set to true or false the method also handles all acknowledgements
	 * to the command that was just sent. 
	 * 
	 * @param command
	 * @param exceptId
	 * @param acknowledge
	 */
	protected void sendCommand(String command, Integer exceptId, boolean acknowledge) {
		if(exceptId != null)
			sendToAllExcept(command, exceptId);
		else
			sendToAll(command);
		
		if(!acknowledge)
			return;
		
		// set the timer to interrupt the host receiving acknowledgements 
		// after the specified time and transfer to the next
		// state after the acknowledgement receiving process
		
		currentTask = new ChangeState(nextStateAfterAck);
		timer.schedule(currentTask, ack_timeout * 1000);
		
		// start receiving acknowledgements
		while(true){
			if(Thread.interrupted()){
				synchronized(currentTask){
					try {
						// interrupt means that we are transfering to the next state
						currentTask.wait();
					} catch (Exception e) {e.printStackTrace();}
				}
				break;
			}
			
			
			// it will keep receiving commands by looping
			// through all connections 
			String received = receiveCommand(true);
			if(received != ""){
				acknowledge(received); // receive acks until interrupted
			}
			if(acknowledgements.size() == numOfPlayers -1){
				System.out.println("FINISHED WAITING EARLIER numOfPlayers: " + numOfPlayers);
				protocolState = nextStateAfterAck;
				timerSet = false;
				currentTask.cancel();
				acknowledgements.clear();
				return;
			}
			if((exceptId != null) && (acknowledgements.size() == numOfPlayers - 2)){
				System.out.println("FINISHED WAITING EARLIER numOfPlayers: " + numOfPlayers);
				protocolState = nextStateAfterAck;
				timerSet = false;
				currentTask.cancel();
				acknowledgements.clear();
				return;
			}
				
		}	
		
		// handle all timeouts
		if(command.contains("ping"))
			handlePingTimeouts();
		else
			handleAckTimeouts(exceptId); 
		
		acknowledgements.clear();
		
		if(!command.contains("timeout")){ // don't do it for timeouts since it will 
												// be sent later either way
			if(numOfPlayers < minNoOfPlayers){ 
				sendLeaveGame(200, "Not enough players");
			}
		}
	}

	
	/**
	 * Checks whether all users responded with an acknowledgement. 
	 * If some haven't they are removed and a timeout command is sent to 
	 * all other players
	 * 
	 * @param exceptId
	 */
	private void handleAckTimeouts(Integer exceptId) {
		for(Integer id : connectionMapping.keySet()){
			if(!acknowledgements.contains(id)){
				if((exceptId == null) || (exceptId != id)){
					System.out.println("remove " + id);

					// removing players that have not sent acknowledgement
					// message about timeout needs to be sent to all players
					timeout timeout = new timeout(id, myID, ack_id);
					ack_id++;
					connectionMapping.remove(connectionMapping.get(id));
					removePlayer(id);
	
					sendCommand(Jsonify.getObjectAsCommand(timeout), null, true); // timeout needs to be acknowledged
				}
			}
		}
	}
	
	/**
	 * Checks whether all users responded with a ping. If someone has not responded
	 * they are removed from the game.
	 */
	private void handlePingTimeouts() {
		for(Integer id : connectionMapping.keySet()){
			if(!acknowledgements.contains(id)){
				System.out.println("remove " + id);

				// removing players that have not sent acknowledgement
				// message about timeout needs to be sent to all players
				connectionMapping.remove(connectionMapping.get(id));
				removePlayer(id);
			}
		}
	}


	/**
	 * Sends a given command to all supported connections (all clients)
	 * @param command
	 */
	private void sendToAll(String command){
		System.out.println("SEND TO ALL: " + command + "\n");
		for (PeerConnection c : connectionMapping.values()) {
			c.sendCommand(command);
		}
	}

	/**
	 * Sends the command to all connections except the one wiht the specified id
	 * @param command
	 * @param playerId
	 */
	private void sendToAllExcept(String command, int playerId) {
		PeerConnection con = connectionMapping.get(playerId);

		System.out.println("SEND TO ALMOST ALL: " + command + "\n");
		for (PeerConnection c : connectionMapping.values()) {
			if(c != con){
				System.out.println("sending.. should never be here");
				c.sendCommand(command);
			}
		}
	}
	
	/**
	 * Sends the command to the connection that is currently handled
	 * @param command
	 */
	private void sendToCurrentConnection(String command){
		System.out.println("SEND: " + command + "\n");
		currentConnection.sendCommand(command);
	}
	
	
	// used to keep track which client to serve (receive command from)
	int previouslyHandledId = 0;
	
	
	
	@Override
	/**
	 * Receives a command from the next client (it serves peer connections one by one)
	 * If the ignoreQueue boolean is set to true, if there was something
	 * that we put into the command queue while receiving acknowledgements we simply ignore
	 * it (don't take it from the queue). This boolean should be set to true for acknowledgements
	 * receiving or roll_numbers receiving.
	 * @param ignoreQueue
	 * @return
	 */
	protected String receiveCommand(boolean ignoreQueue) {
		if(connectionMapping.size() == 0)
			return "";
	
		String command = "";
		if(!ignoreQueue && commandQueue.size() != 0){			
			try {
				command = commandQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			previouslyHandledId = getPlayerid(command);
			System.out.println("got id: " + previouslyHandledId);
			currentConnection = connectionMapping.get(previouslyHandledId);
		}
		
		else{
			int idToHandle = (((previouslyHandledId + 1) % numOfPlayers));
			if(idToHandle == 0)
				idToHandle++;
			
			System.out.println("ID " + idToHandle);
			PeerConnection con = connectionMapping.get(idToHandle);
			command = con.receiveCommand();
			System.out.println(".. ");
			currentConnection = con;
			previouslyHandledId = idToHandle;
		}
		
	
		if(command != "")
			System.out.println("GOT FROM " + previouslyHandledId + ": " + command + "\n");
		return command;
	}
	

	/**
	 * 
	 * @param command
	 * @return
	 */
	private int getPlayerid(String command) {
		String[] parts = command.split(",");
		for(int i = 0; i < parts.length; i++){
			if(parts[i].contains("player_id")){
				String parts2[] = parts[i].split(":");
				char x = (parts2[1].charAt(0));
				return Character.getNumericValue(x);
			}
		}
		return -1;
	}


// =====================================================================
// RUNNING THE HOST 
// =====================================================================


	public void run(){
		myName = getRandomName();
		createNewPlayer(myName, 0, true);		// creating our local player

		try {
			serverSocket = new ServerSocket(4444);
			serverSocket.setSoTimeout(2000); // socket waits for 2 seconds -- needed for checking interrupts
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.run();
		timer.cancel();

		// closing all sockets
		for(PeerConnection con : connectionMapping.values()){
			con.close();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		HostProtocol protocol = new HostProtocol(new State());
		protocol.run();
	}

	@Override
	protected void acknowledge(int ack) {
	}

	@Override
	protected void handleTimeout(String command) {
	}	

}

