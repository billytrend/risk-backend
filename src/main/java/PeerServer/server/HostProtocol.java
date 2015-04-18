package PeerServer.server;import GameEngine.GameEngine;import GameState.Player;import GameState.State;import GeneralUtils.Jsonify;import PeerServer.protocol.gameplay.setup;import PeerServer.protocol.general.acknowledgement;import PeerServer.protocol.general.leave_game;import PeerServer.protocol.general.timeout;import PeerServer.protocol.setup.*;import PlayerInput.DumbBotInterface;import PlayerInput.PlayerInterface;import PlayerInput.RemotePlayer;import java.io.DataInputStream;import java.io.DataOutputStream;import java.io.IOException;import java.net.ServerSocket;import java.net.Socket;import java.util.*;import java.util.concurrent.BlockingQueue;import java.util.concurrent.LinkedBlockingQueue;import static com.esotericsoftware.minlog.Log.debug;/** * An instance of this class represents a game that * is currently being played over the network. *  */public class HostProtocol extends AbstractProtocol {	private int maxNoOfPlayers = 6;		//min is 3		private int minNoOfPlayers = 2;	private int waitTimeInSeconds = 3; // host waits 180 seconds for remaining players to connect	// mappings of features / versions to the count of how many clients support these	private HashMap<String, Integer> supportedFeatures = new HashMap<String, Integer>();	private HashMap<Integer, Integer> supportedVersions = new HashMap<Integer, Integer>();	private ArrayList<PeerConnection> connections = new ArrayList<PeerConnection>(); 	// players connections	// maps players IDs with a connection associated with them (null for local)	private HashMap<Integer, PeerConnection> connectionMapping = new HashMap<Integer, PeerConnection>();	// connection which is currently served	private PeerConnection currentConnection;	private Set<PeerConnection> acknowledgements = new HashSet<PeerConnection>(); // IDs	private Socket newSocket;	private ServerSocket serverSocket;	private String newName = "";	private String newKey = "";	private Thread gameEngineThread;		@Override	protected void takeSetupAction() {		//Log.DEBUG = true;		ArrayList<String> allCommands;		// check if timer didn't interrupt		if(Thread.interrupted()){			if(timerSet){				try {				// if timer wants to change the current state, don't do anything				// let it change it to avoid concurrency problems				//timer.wait();					currentTask.wait();				} catch (Exception e) {}			}			else{				try {					// wait on game engine to check the correctness of commands it got					gameEngineThread.wait();				} catch (InterruptedException e) {					e.printStackTrace();				}				// if the command was wrong leave the game!				if(wrongCommand)					protocolState = leave_game("");			}		}		switch(protocolState){		case JOIN_GAME:			debug("\nJOIN_GAME");			// accept a new client and handle their request			try {				newSocket = serverSocket.accept();				newSocket.setSoTimeout(1000);				DataInputStream fromClient = new DataInputStream(newSocket.getInputStream());				this.protocolState = join_game(fromClient.readUTF());			} catch (IOException e) {}			break;				case ACCEPT_JOIN_GAME:			debug("\n ACCEPT_JOIN_GAME");			protocolState = accept_join_game("");			break;		case PLAYERS_JOINED:			debug("\n PLAYERS_JOINED");			protocolState = players_joined("");			// if we got min number of players, schedule new task for timer			// after a specified time it will transfer the state to the next one (PING)			if(startingPlayers.size() == minNoOfPlayers){				currentTask = new ChangeState();				timer.schedule(currentTask, waitTimeInSeconds * 1000);			}			break;		case REJECT_JOIN_GAME:			debug("\n REJECT_JOIN_GAME");			protocolState = reject_join_game("");			break;		case PING:			debug("\n PING");			protocolState = ping("");			currentTask = new ChangeState();			timer.schedule(currentTask, ack_timeout * 1000);			break;		case PING_ACK:			debug("\n PING_ACK");			// get command from each connection (if there are any commands)			allCommands = getAllPlayersCommands();			for(String command : allCommands){				ping(command);			}			break;		case READY:			debug("\n READY");			this.protocolState = ready("");			currentTask = new ChangeState();			// THIS BIT BEFORE WE GO TO ACKNOWLEDGE STATE =============			nextStateAfterAck = ProtocolState.INIT_GAME;			timer.schedule(currentTask, ack_timeout * 1000);			// =======================================================			break;		case ACK:			debug("\n ACKNOWLEDGE");			allCommands = getAllPlayersCommands();			for(String command : allCommands){				acknowledge(command);			}			break;		case INIT_GAME:			debug("\n INIT_GAME");			protocolState = init_game("");			break;		case SETUP_GAME:			debug("\n SETUP_GAME");			this.protocolState = setup_game("");			// THIS BIT BEFORE WE GO TO ACKNOWLEDGE STATE =============			nextStateAfterAck = ProtocolState.PLAY_CARDS;			timer.schedule(currentTask, ack_timeout * 1000);			// =======================================================			break;		case LEAVE_GAME:			debug("\n LEAVE_GAME");			this.protocolState = leave_game("");			break;		default:			break;		}	}		@Override	protected void takeGameAction() {        ArrayList<String> allCommands;        switch (protocolState) {            case PLAY_CARDS:                debug("\n PLAY_CARDS");			//if(command.c)			protocolState = play_cards("");			//Waits for ACK then goes to DEPLOY state			nextStateAfterAck = ProtocolState.DEPLOY;			timer.schedule(currentTask, ack_timeout * 1000);			break;		case DEPLOY:			debug("\n DEPLOY");			protocolState = deploy("");			//Waits for ACK then goes to ATTACK state			nextStateAfterAck = ProtocolState.ATTACK;			timer.schedule(currentTask, ack_timeout * 1000);			break;		case ATTACK:			debug("\n ATTACK");			protocolState = attack("");            //Could be ATTACK CAPTURE, FORTIFYING and ATTACK_CAPTURE            nextStateAfterAck = ProtocolState.ATTACK;            timer.schedule(currentTask, ack_timeout * 1000);            //TODO: remember to check if it is ATTACK CAPTURE/ FORTIFY/ATTACK again            break;		case DEFEND:			debug("\n DEFEND");			//create a defend command for host			protocolState = defend("");			nextStateAfterAck = ProtocolState.INIT_GAME;			timer.schedule(currentTask, ack_timeout * 1000);			break;		case ATTACK_CAPTURE:			debug("\n ATTACK_CAPTURE");			protocolState = attack_capture("");            //return to ATTACK as once captured they may attack again            nextStateAfterAck = ProtocolState.ATTACK;            timer.schedule(currentTask, ack_timeout * 1000);            break;            case FORTIFY:			debug("\n FORTIFY");			protocolState = fortify("");            //return to next player's turn (play_cards is first move)            nextStateAfterAck = ProtocolState.PLAY_CARDS;            timer.schedule(currentTask, ack_timeout * 1000);            break;            case ACK:			debug("\n ACK");            //check we have all the ACKS            allCommands = getAllPlayersCommands();            for (String command : allCommands) {                acknowledge(command);            }            break;            case ROLL_HASH:                debug("\n ROLL_HASH");			protocolState = roll_hash("");			break;		case ROLL_NUMBER:			debug("\n ROLL_NUMBER");			protocolState = roll_number("");			break;		case TIMEOUT:			debug("\n TIMEOUT");			protocolState = timeout("");            nextStateAfterAck = ProtocolState.LEAVE_GAME;            timer.schedule(currentTask, ack_timeout * 1000);            break;            case LEAVE_GAME:			debug("\n LEAVE_GAME");			this.protocolState = leave_game("");			break;			default:			System.out.println("in default not good");			break;		}	}	private Object getCurrentTask() {		return currentTask;	}	@Override	protected ProtocolState join_game(String command) {			System.out.println("\nGOT: " + command);		//create JSON object of command		join_game join_game = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);		if(join_game == null){			errorMessage = ("Wrong request");			return ProtocolState.REJECT_JOIN_GAME;		}		if(startingPlayers.size() >= maxNoOfPlayers){			errorMessage = ("Full");			return ProtocolState.REJECT_JOIN_GAME;		}		// game will be accepted so update versions and features counts		update(join_game.payload.supported_features, supportedFeatures);		update(join_game.payload.supported_versions, supportedVersions);		newName = join_game.payload.name;		newKey = join_game.payload.public_key;		return ProtocolState.ACCEPT_JOIN_GAME;	}	@Override	protected ProtocolState accept_join_game(String command) {				// new id generated		int id = startingPlayers.size();		// new connection is created for a new accepted player		PeerConnection newConnection = new PeerConnection(newSocket, id);		connections.add(newConnection);		connectionMapping.put(id, newConnection);		currentConnection = newConnection;		Player newOne = createNewPlayer(newName, id, false);		newOne.setPublicKey(newKey);		//sending response - about being accepted		accept_join_game accept_join_game =				new accept_join_game(id, ack_timeout, move_timeout);		newConnection.sendCommand(Jsonify.getObjectAsJsonString(accept_join_game));		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(accept_join_game));		return ProtocolState.PLAYERS_JOINED;	}	@Override	protected ProtocolState reject_join_game(String reason) {		//create json string 		reject_join_game reject_join_game = new reject_join_game(errorMessage);				String rj = Jsonify.getObjectAsJsonString(reject_join_game);		// dont create peer connection for this client, they are rejected		DataOutputStream out;		try {			out = new DataOutputStream(newSocket.getOutputStream());			//send rejection 			out.writeUTF(rj);		} catch (IOException e) {			// TODO Auto-generated catch block			e.printStackTrace();		}		System.out.println("\nSEND: " + rj);		return protocolState.JOIN_GAME;	}	@Override	protected ProtocolState players_joined(String command){		players_joined players_joined = new players_joined(startingPlayers);		String npj = Jsonify.getObjectAsJsonString(players_joined);		System.out.println("\nSEND: " + npj);		currentConnection.sendCommand(npj);		// sending only new player to the rest		int id = currentConnection.getId();		players_joined toRest = new players_joined(new String[]{Integer.toString(id), newName, newKey});		sendToAllExcept(Jsonify.getObjectAsJsonString(toRest), id);				System.out.println("\nSEND TO ALMOST ALL: " + Jsonify.getObjectAsJsonString(toRest));		return protocolState.JOIN_GAME;	}	@Override	protected ProtocolState ping(String command){		// host is pinging		if(command == ""){			// add all players to the state - will be deleted later if needed			state.setPlayers(startingPlayers);			// create and send a ping to all clients			ping ping = new ping(startingPlayers.size(), 0);			String p = Jsonify.getObjectAsJsonString(ping);			sendToAll(p);			System.out.println("\nSEND TO ALL: " + p);			return ProtocolState.PING_ACK;		}		else{			ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);			if(ping == null){				leaveCode = 200;				leaveReason = "Wrong command: expected ping";				return ProtocolState.LEAVE_GAME;			}			System.out.println("\nGOT: " + command);			int id = ping.player_id;			// forward ping to everyone			sendToAllExcept(command, id);			System.out.println("\nSEND TO AMOST ALL: " + command);			acknowledgements.add(currentConnection);		}		return ProtocolState.PING_ACK;	}	@Override	protected ProtocolState ready(String command){		// check for any dropouts 		for(PeerConnection c : connections){			if(!acknowledgements.contains(c)){				// removing players that have not sent ping				timeout timeout = new timeout(0, 1, c.getId());				removePlayer(c);			}		}		// if less than 3 players remained send leave		if(startingPlayers.size() < minNoOfPlayers){			return ProtocolState.LEAVE_GAME;		}		//send ready command to all connected clients ACK required		ready ready = new ready(null, 0, ack_id);		ack_id++; // TODO: I guess its supposed to work like this		sendToAll(Jsonify.getObjectAsJsonString(ready));		System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(ready));		// clear all ping acknowledgements		acknowledgements.clear();		return ProtocolState.ACK;	}	/**	 * Receives acknowledgements (either ping or acknowledgement)	 * it keeps receiving these until the timeout passes.	 * 	 * @param command	 * @return	 */	protected ProtocolState acknowledge(String command) {		if(Thread.interrupted()){			synchronized(this){				timer.notify();				try {					timer.wait();				} catch (InterruptedException e) {					// TODO Auto-generated catch block					e.printStackTrace();				}			}		}		acknowledgement ack = (acknowledgement) Jsonify.getJsonStringAsObject(command, acknowledgement.class);		if(ack == null){			leaveCode = 200;			leaveReason = "Wrong command: expected acknowledge";			return ProtocolState.LEAVE_GAME;		}		System.out.println("\nGOT: " + command);		acknowledgements.add(currentConnection);		return ProtocolState.ACK;	}	@Override	protected ProtocolState timeout(String command){		for(PeerConnection c : connections){			if(!acknowledgements.contains(c)){				// removing players that have not sent acknowledgement				// message about timeout needs to be sent to all players				timeout timeout = new timeout(0, 1, c.getId());				removePlayer(c);				System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(timeout));				sendToAll(Jsonify.getObjectAsJsonString(timeout));			}		}		if(startingPlayers.size() < minNoOfPlayers){			return leave_game("");		}		return init_game("");	}	@Override	protected ProtocolState init_game(String command){		// finding the highest version supported by all		int version = findHighestVersionSupportedByAll(supportedVersions);		// get features that are supported by all		ArrayList<String> features = findFeaturesSupportedByAll(supportedFeatures);		String[] feat = new String[features.size()];		features.toArray(feat);		initialise_game init_game = new initialise_game(version, feat);		sendToAll(Jsonify.getObjectAsJsonString(init_game));		// START THE GAME ENGINE		State state = new State(startingPlayers);		engine = new GameEngine(state);		diceRoller.setFaceValue(startingPlayers.size());		System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(init_game));		return ProtocolState.LEAVE_GAME; // TODO: JUST FOR NOW - TO STOP - CHANGE IT LATER.	}	@Override	protected ProtocolState leave_game(String command) {		// if command is not empty sb sent a leave_command		if(command != ""){			leave_game leave = (leave_game) Jsonify.getJsonStringAsObject(command, leave_game.class);			if(leave == null){				leaveCode = 200;				leaveReason = "Wrong command";				return leave_game("");			}			//TODO: print a reason?			int responseCode = leave.payload.response;			String message = leave.payload.message;			int id = leave.player_id;			sendToAllExcept(command, id);						// TODO: make sure its ok!			removePlayer(currentConnection);						System.out.println("\nSEND ALMOST ALL: " + Jsonify.getObjectAsJsonString(command));			// dont change state			return protocolState;		}		// if command is empty it is us that want to leave		else{			leave_game leave = new leave_game(leaveCode, leaveReason, false, 0);			System.out.println("\nSEND TO ALL: " + Jsonify.getObjectAsJsonString(leave));			sendToAll(Jsonify.getObjectAsJsonString(leave));			// wait a bit for everyone to receive hosts leaving message			try {				Thread.sleep(5000);			} catch (InterruptedException e) {				e.printStackTrace();			}			return null;		}	}	@Override	protected ProtocolState setup_game(String command){		//if command is empty then we have to setup		if(command == ""){			//create setup command			setup setup = (setup) getResponseFromLocalPlayer();			//convert to JSON stirng			String setupString = Jsonify.getObjectAsJsonString(setup);			//Send to all clients			sendCommand(setupString, null);		}		//someone sent us a command		else {			setup setup = (setup) Jsonify.getJsonStringAsObject(command, setup.class);			if(setup == null){				return protocolState.LEAVE_GAME;			}			sendCommand(command,setup.player_id);			notifyPlayerInterface(setup, setup.player_id);		}		return protocolState;		}	/**	 * Removes the given connection from the list of supported connections	 * and a player associated with it from the game state	 * @param connection	 */	protected void removePlayer(PeerConnection connection){		int ID = -1;		for(int id : connectionMapping.keySet()){			if(connectionMapping.get(id) == connection)				ID = id;		}		// remove that player from connections		connections.remove(connection);		if(ID != -1){			connectionMapping.remove(ID);			// remove that player from the game state			removePlayer(ID);		}	}	/**	 * Sends a given command to all supported connections (all clients)	 * @param command	 */	private void sendToAll(String command){		for (PeerConnection c : connections) {			c.sendCommand(command);		}	}	private void sendToAllExcept(String command, int playerId) {		PeerConnection con = connectionMapping.get(playerId);		for (PeerConnection c : connections) {			if(c != con)				c.sendCommand(command);		}	}	/**	 * 	 * @param map	 * @return	 */	private ArrayList<String> findFeaturesSupportedByAll(			HashMap<String, Integer> map) {		ArrayList<String> features = new ArrayList<String>();		for(String a : map.keySet()){			if(map.get(a) == connections.size())				features.add(a);		}			return features;	}	/**	 * 	 * @param map	 * @return	 */	private int findHighestVersionSupportedByAll(HashMap<Integer, Integer> map){		int supported = 1;		Iterator<Integer> it = map.keySet().iterator();		while(it.hasNext()){			Integer current = it.next();			if(map.get(current) == connections.size())				supported = current;		}		return supported;	}	/**	 * Updates a given mapping based on the input array.	 * Increases count of features / versions specified in the array.	 * 	 * @param input	 * @param toUpdate	 */	private void update(Object[] input,	HashMap toUpdate) {		Arrays.sort(input);		Integer count;		for(Object a : input){			if(toUpdate.containsKey(a)){				count = (Integer) toUpdate.get(a);				toUpdate.put(a, count + 1);			}			else{				toUpdate.put(a, 1);			}		}	}	public static void main(String[] args) {		HostProtocol protocol = new HostProtocol();		protocol.run();	}	/**	 * Loops over all connections (one for each player), receive	 * their latest commands and return them all in a list	 * @return	 */	private ArrayList<String> getAllPlayersCommands(){		ArrayList<String> commands = new ArrayList<String>();		for (PeerConnection c : connections) {			if(protocolState == ProtocolState.LEAVE_GAME)				break;			String playersLatestCommand = c.receiveCommand();			if (!playersLatestCommand.equals("")) {				currentConnection = c;				commands.add(playersLatestCommand);			}				}		return commands;	}	@Override	public void run(){		// creating our local player		createNewPlayer(getRandomName(), 0, true);		connectionMapping.put(0, null);		try {			serverSocket = new ServerSocket(4444);			serverSocket.setSoTimeout(6000); // socket waits for 2 seconds -- needed for checking interrupts		} catch (IOException e) {			e.printStackTrace();		}		super.run();		timer.cancel();		// closing all sockets		for(PeerConnection con : connections){			con.close();		}		try {			serverSocket.close();		} catch (IOException e) {			// TODO Auto-generated catch block			e.printStackTrace();		}	}	@Override	protected void sendCommand(String command, Integer exceptId) {		if(exceptId != null)			sendToAllExcept(command, exceptId);		else			sendToAll(command);	}	@Override	protected String receiveCommand() {		// TODO Auto-generated method stub		return null;	}}