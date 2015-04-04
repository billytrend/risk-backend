package PeerServer.server;

import static PeerServer.server.ProtocolState.*;
import static com.esotericsoftware.minlog.Log.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.search.SentDateTerm;
import javax.persistence.Id;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.*;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.*;
import PlayerInput.PlayerInterface;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * An instance of this class represents a game that
 * is currently being played over the network.
 * 
 */
public class Protocol {

	private ProtocolState protocolState = JOIN_GAME;
	private boolean isHost = true;
	private int ack_timeout;
	private int move_timeout;
	private int maxNoOfPlayers = 6;		//min is 3	
	
	private boolean apiSupported;
	private int gameLaunchTime = 60;
	
	private HashMap<String, Integer> supportedFeatures = new HashMap<String, Integer>();
	private HashMap<Integer, Integer> supportedVersions = new HashMap<Integer, Integer>();
	private String errorMessage = "default";
	
	private State state;
	private ArrayList<Player> startingPlayers;
	private ArrayList<Connection> connections;
	private GameEngine engine = null;
	private HashMap<Integer, Connection> idMap = new HashMap<Integer, Connection>();
	private Connection currentConnection;
	
	private ArrayList<Connection> acknowlegements;
	
	private long startTime;
	
	
	/**
	 * This is the game loop.
	 * Ensures the game runs.
	 */
	private void play() throws InterruptedException {

		if(isHost){
			//set the ack timeout to 30 seconds move to 60
			ack_timeout = 30;
			move_timeout = 60;

		}

		//game loop
		while (true) {
			//if(!iterateGame()) return;
		}
	}

	/**
	 * Manages the different states and associated commands.
	 * @param command
	 * @return
	 */
	private boolean handleCommand(String command){
		switch(this.protocolState){
		//case WAITING_FOR_PLAYERS:
		///	debug("\nWAITING_FOR_PLAYERS");
		//	this.protocolState = waiting_for_players(command);
		//	break;
		case JOIN_GAME:
			debug("\nJOIN_GAME");
			this.protocolState = join_game(command);			
			break;		
		case ACCEPT_JOIN_GAME:
			debug("\n ACCEPT_JOIN_GAME");
			this.protocolState = accept_join_game(command);
			break;
		case REJECT_JOIN_GAME:
			debug("\n REJECT_JOIN_GAME");
			this.protocolState = reject_join_game(errorMessage);
			break;
		case PLAYERS_JOINED:
			debug("\n PLAYERS_JOINED");
			this.protocolState = players_joined(command);
			break;
		case PING:
			debug("\n PING");
			this.protocolState = ping(command);
			break;
		case PING_ACK:
			debug("\n PING_ACK");
			this.protocolState = ping_ack(command);
			break;
		case READY:
			debug("\n READY");
			this.protocolState = ready(command);
			break;
		case RECEIVE_ACK:
			debug("\n RECEIVE_ACK");
			this.protocolState = receive_ack(command);
			break;
		case INIT_GAME:
			debug("\n INIT_GAME");
			this.protocolState = init_game(command);
			break;
		case SETUP_GAME:
			debug("\n SETUP_GAME");
			this.protocolState = setup_game(command);
			break;
		case PLAY_CARDS:
			debug("\n PLAY_CARDS");
			this.protocolState = play_cards(command);
			break;
		case DRAW_CARD:
			debug("\n DRAW_CARD");
			this.protocolState = draw_card(command);
			break;
		case DEPLOY:
			debug("\n DEPLOY");
			this.protocolState = deploy(command);
			break;
		case ATTACK:
			debug("\n ATTACK");
			this.protocolState = attack(command);
			break;
		case DEFEND:
			debug("\n DEFEND");
			this.protocolState = defend(command);
			break;
		case ATTACK_CAPTURE:
			debug("\n ATTACK_CAPTURE");
			this.protocolState = attack_capture(command);
			break;
		case FORTIFY:
			debug("\n FORTIFY");
			this.protocolState = fortify(command);
			break;
		case ACK:
			debug("\n ACK");
			this.protocolState = ack(command);
			break;
		case ROLL:
			debug("\n ROLL");
			this.protocolState = roll(command);
			break;
		case ROLL_HASH:
			debug("\n ROLL_HASH");
			this.protocolState = roll_hash(command);
			break;
		case ROLL_NUMBER:
			debug("\n ROLL_NUMBER");
			this.protocolState = roll_number(command);
			break;
		case TIMEOUT:
			debug("\n TIMEOUT");
			this.protocolState = timeout(command);
			break;
		case LEAVE_GAME:
			debug("\n LEAVE_GAME");
			this.protocolState = leave_game(command);
			break;
		default:
			System.out.println("IN DEFAULT not good");
			break;
		}
		return false;
	}

	/**
	 * Sent by a client to a host attempting to join a game. 
	 * First command sent upon opening a socket connection to a host.
	 * @param command 
	 * @return ACCEPT_JOIN_GAME if request successful otherwise REJECT_JOIN_GAME
	 */
	private ProtocolState join_game(String command) {
		
		try {
			//create JSON object of command
			join_game join_game = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);
			
			//reject if game in progress 
			if(engine != null) {
				System.out.println("Cant join Reason: Game in Progress!");
				errorMessage = "Reason: Game in Progress!";
				return REJECT_JOIN_GAME;
			}
			
			//reject if no more space in game
			if(PlayerUtils.getPlayersInGame(state).size() >= maxNoOfPlayers) {
				System.out.println("Cant join Reason: Game is currentConnection.idFull!");
				// TODO: send over network
				errorMessage = "Reason: Game is Full!";
				return REJECT_JOIN_GAME;
			}
			
			update(join_game.supported_features, supportedFeatures);
			update(join_game.supported_versions, supportedVersions);
			
		}
		catch(Exception error) {
			//if command string does not create a join_game object correctly
			System.out.println("The join_game command was malformed");
			return REJECT_JOIN_GAME;
		}
		
		
		if(startingPlayers.size() < 3){
			System.out.println("STILL WAITING ON MORE PLAYERS");
			//send an accept join game to client
			return ACCEPT_JOIN_GAME;
		}
		
		// TODO: change this -- wait for other people
		// WAIT HERE
		return PING;	
	}

	
	
	
	private void update(Object[] input,	HashMap toUpdate) {
		Arrays.sort(input);
		Integer count;
		for(Object a : input){
			if(toUpdate.containsKey(a)){
				count = (Integer) toUpdate.get(a);
				toUpdate.put(a, count + 1);
			}
			else{
				toUpdate.put(toUpdate, 1);
			}
		}
	}

	/**
	 * Sent by a host to a client on receipt of a “join_game” command, 
	 * as confirmation of adding them to the game.
	 * @param command 
	 * @return
	 */
	private ProtocolState accept_join_game(String command) {
		//add player's name to players list
		accept_join_game accept_join_game =
				new accept_join_game(currentConnection.id, ack_timeout, move_timeout);
		
		int id = startingPlayers.size();
		
		// TODO: starting armies fix
		// TODO: add logic between host being a player
		startingPlayers.add(new Player(new RemotePlayer(), 0, id));
		
		idMap.put(id, currentConnection);
		
		//turn object into JSON string to be sent over network 
		String ajg = Jsonify.getObjectAsJsonString(accept_join_game);

		//sending to connection
		currentConnection.sendCommand(ajg);
		System.out.println("Sending... " + ajg);

		//if they had a name move to players joined
		return PLAYERS_JOINED;
	}

	
	/**
	 * Sent by a host to a client on receipt of a “join_game” command, as rejection.
	 * @param command
	 * @return END_GAME as the player wont take part in the game
	 */
	private ProtocolState reject_join_game(String reason) {
		//create json string 
		reject_join_game reject_join_game = new reject_join_game(errorMessage);		
		String rj = Jsonify.getObjectAsJsonString(reject_join_game);
		
		//send rejection 
		currentConnection.sendCommand(rj);
	
		System.out.println(rj);

		//end the game for this thread / player
		// TODO: think about it
		if(engine != null) return END_GAME;
		//otherwise return to join game
		return JOIN_GAME;
	}


	/**
	 * Sent by a host to each player after connection as players join the game. 
	 * Maps player IDs to real names. Optional command, 
	 * will only be sent if the player specified a real name itself.
	 * @return state change 
	 */
	private ProtocolState players_joined(String command){
	
		players_joined players_joined = new players_joined(startingPlayers);
		String npj = Jsonify.getObjectAsJsonString(players_joined);
		System.out.println(npj);
		
		//send to new player containing current players
		sendToAll(npj);		
		
		return JOIN_GAME;
	}


	//from now on all messages must be broadcast to all clients
	private ProtocolState ping(String command){
		System.out.println("Number of players in game: " + startingPlayers.size());

		ping ping = new ping(startingPlayers.size(), 0);
		String p = Jsonify.getObjectAsJsonString(ping);
		//send ping to each client
		System.out.println(p);
		sendToAll(p);
		
		startTime = System.currentTimeMillis();
		return RECEIVE_ACK;
	}
	
	
	private ProtocolState ping_ack(String command) {
		return acknowledge(command, true);
	}
	private ProtocolState receive_ack(String command){
		return acknowledge(command, false);
	}
	
	
	private ProtocolState acknowledge(String command, boolean isPing) {
		long time = System.currentTimeMillis();
		
		// if the time is not out receive the acknowledgement
		if((startTime - time)/1000 < ack_timeout){
			try{
				Object result;
				if(isPing)
					result = (PeerServer.protocol.setup.ping) Jsonify.getJsonStringAsObject(command, ping.class);
				else
					result = Jsonify.getJsonStringAsObject(command, acknowledgement.class);
				
				// if(result != null) ??
				acknowlegements.add(currentConnection);
			}
			catch(Exception error) {
				System.out.println("Wrong ping acknowledgement");
			}
			
			if(isPing)
				return PING_ACK;
			else
				return RECEIVE_ACK;
		}	
		
		
		else{
			for(Connection c : connections){
				if(!acknowlegements.contains(c)){
					// removing players that have not sent acknowledgement
					if(!isPing){
						// message about timeout needs to be sent to all players
						timeout timeout = new timeout(0, 1, c.id);
						sendToAll(Jsonify.getObjectAsJsonString(timeout));
					}
					remove_player(c);
				}
			}
			
			if(startingPlayers.size() < 3){
				System.out.println("End of Game: Sending Leave_Game to all clients");
				return LEAVE_GAME;
			}

			//if all clients respond 
			if(isPing)
				return READY;
			else
				return INIT_GAME;
		}
	}
	
	
	

	private ProtocolState ready(String command){
		//send ready command to all connected clients ACK required
		ready ready = new ready(null, 0, 1);
		sendToAll(Jsonify.getObjectAsJsonString(ready));

		return RECEIVE_ACK;
	}

	
	private ProtocolState init_game(String command){
		// finding the highest version supported by all
		int version = findVersionSupportedByAll(supportedVersions);
		
		// get features that are supported by all
		ArrayList<String> features = findFeaturesSupportedByAll(supportedFeatures);
		
		initalise_game init_game = new initalise_game(version, (String[]) features.toArray());
		sendToAll(Jsonify.getObjectAsJsonString(init_game));
		
		
		return READY;
	}

	
	private ArrayList<String> findFeaturesSupportedByAll(
			HashMap<String, Integer> map) {
		ArrayList<String> features = new ArrayList<String>();
		
		for(String a : map.keySet()){
			if(map.get(a) == connections.size())
				features.add(a);
		}	
		return features;
	}

	private int findVersionSupportedByAll(HashMap<Integer, Integer> map){
		int supported = 1;
		for(Integer a : map.keySet()){
			if(map.get(a) == connections.size())
				supported = a;
		}
		
		return supported;
	}

	/**
	 * Sent by each player in turn at the start of the game to 
	 * claim a territory or reinforce an owned territory (once all have been claimed).
	 * @param command
	 * @return
	 */
	private ProtocolState setup_game(String command){
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.setup.class);
		return protocolState;	
	}



	//***************************** CARDS ***********************************

	/**
	 * Sent by each player at the start of their turn, specifying group(s) of 
	 * cards to trade in for armies, and the number of armies they are expecting to receive. 
	 * This command must always be sent at the start of a turn, even if no cards are being traded.
	 * @param command
	 * @return
	 */
	private ProtocolState play_cards(String command){
		Object play_cards = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.cards.play_cards.class);
		return protocolState;	
	}


	private ProtocolState draw_card(String command){
		Object draw_card = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.cards.draw_card.class);
		return protocolState;	
	}

	private ProtocolState deploy(String command){
		Object deploy = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.cards.deploy.class);
		return protocolState;	
	}


	//*********************** ATTACK / DEFEND ******************************

	private ProtocolState attack(String command) {
		return null;
	}

	private ProtocolState defend(String command){
		return protocolState;

	}

	private ProtocolState attack_capture(String command){
		return protocolState;

	}


	private ProtocolState fortify(String command){
		return protocolState;

	}
	
	private ProtocolState ack(String command){
		return protocolState;
	}

	//*********************** DICE ROLLS ******************************

	private ProtocolState roll(String command){
		return protocolState;

	}

	private ProtocolState roll_hash(String command){
		return protocolState;

	}

	private ProtocolState roll_number(String command){
		return protocolState;

	}

	//*********************** ERRORS ******************************
	private ProtocolState timeout(String command){
		return protocolState;

	}

	private ProtocolState leave_game(String command){
		return protocolState;

	}
	
	
	private void remove_player(Connection connection){
		int id = connection.id;
		Player player = state.lookUpPlayer(id);
		
		// TODO: make sure its ok with game engine
		PlayerUtils.removePlayer(state, player);
		
		// remove that player from connections etc
		connections.remove(connection);
		idMap.remove(id);
		
		// if a game hasnt started and a player needs to be removed
		// then the starting array should be altered
		if(engine == null){
			startingPlayers.remove(state.lookUpPlayer(id));
		}
	}
	
	
	private void sendToAll(String command){
		for (Connection c : connections) {
			c.sendCommand(command);
		}
	}

	/**
	 * @param string to be tested
	 * @return true if string is valid JSON
	 * 			false otherwise
	 */
	public boolean isJsonStringValid(String test) {
		try {
			JsonObject o = new JsonParser().parse(test).getAsJsonObject();
		} catch (JsonParseException ex) {
			try {
				//incase array is valid too
				JsonArray o = new JsonParser().parse(test).getAsJsonArray();
			} catch (JsonParseException ex1) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Protocol protocol = new Protocol();
		
		protocol.state = new State();
		RiskMapGameBuilder.addRiskTerritoriesToState(protocol.state);
		
		// null means that its a local player
		protocol.idMap.put(0, null);
		
		for (Connection c : protocol.connections) {
			String playersLatestCommand = c.receiveCommand();
			if (!playersLatestCommand.equals("")) {
				protocol.currentConnection = c;
				protocol.handleCommand(playersLatestCommand);
			}		
		}

		// build the game state
		// accept players according to protocol
		//      as players connect break off the connections into threads
		//      create a mapping between 'remote player objects' and the threads
		//      add remote player objects to the game state
		//
		// add players to game state as
		//
		// start the game
		// field all received player actions and send them to appropriate player objects
		//
		//
	}

}
