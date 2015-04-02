package PeerServer.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import static com.esotericsoftware.minlog.Log.debug;
import PeerServer.protocol.setup.*;
import PeerServer.protocol.general.*;
import PeerServer.protocol.dice.*;
import PeerServer.protocol.cards.*;
import PeerServer.protocol.gameplay.*;
import PlayerInput.PlayerInterface;
import GameBuilders.RiskMapGameBuilder;
import GameEngine.*;
import GameState.*;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;


public class ClientProtocol{

	protected int ack_timeout;
	protected int move_timeout;
	protected ProtocolState protocolState = ProtocolState.JOIN_GAME;
	protected String errorMessage = "default";
	
	protected State state;
	protected ArrayList<Player> startingPlayers = new ArrayList<Player>();
	protected HashMap<Integer, PlayerInterface> interfaceMapping = new HashMap<Integer, PlayerInterface>();
	protected GameEngine engine = null;
	private ArrayList<Integer> acknowlegements; // IDs
	Client client;
	Random ran = new Random();
	private int myID;
	
	private int versionPlayed;
	private String[] featuresUsed;
	
	private ArrayList<String> funNames = new ArrayList<String>();
	
	/**
	 * Manages the different states and associated commands.
	 * @param command
	 * @return
	 */
	public boolean handleCommand(String command){
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
			debug("\n REJPlayerInterfaceECT_JOIN_GAME");
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
	
	private ProtocolState receive_ack(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	private ProtocolState ping_ack(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	/**Jsonify
	 * Sent by a client to a host attempting to join a game. 
	 * First command sent upon opening a socket connection to a host.
	 * @param command 
	 * @return ACCEPT_JOIN_GAME if request successful otherwise REJECT_JOIN_GAME
	 */
	private ProtocolState join_game(String command) {
		
		// send request to join the game
		String name = funNames.get(ran.nextInt(funNames.size()));
		join_game join = new join_game(new Integer[]{1}, new String[]{}, name, "key");
		client.send(Jsonify.getObjectAsJsonString(join));
		
		String response = client.receive();
		
		if(response.contains("accept_join_game")){
			return accept_join_game(response);
		}
		
		if(response.contains("reject_join_game")){
			return reject_join_game(response);
		}
		
		// host did not respond correctly
		return ProtocolState.LEAVE_GAME;
	}

	

	/**
	 * Sent by a host to a client on receipt of a “join_game” command, 
	 * as confirmation of adding them to the game.
	 * @param command 
	 * @return
	 */
	private ProtocolState accept_join_game(String command) {
		accept_join_game join = (PeerServer.protocol.setup.accept_join_game) Jsonify.getJsonStringAsObject(command, accept_join_game.class);
		if(join == null)
			return ProtocolState.LEAVE_GAME;
		
		// retrieve all details from host response
		myID = join.player_id;
		ack_timeout = join.acknowledgement_timeout;
		move_timeout = join.move_timeout;
		
		return ProtocolState.PLAYERS_JOINED;
	}

	
	/**
	 * Sent by a host to a client on receipt of a “join_game” command, as rejection.
	 * @param command
	 * @return END_GAME as the player wont take part in the game
	 */
	private ProtocolState reject_join_game(String reason) {
		reject_join_game reject = (PeerServer.protocol.setup.reject_join_game) Jsonify.getJsonStringAsObject(reason, reject_join_game.class);
		
		// present in UI preferably
		System.out.println(reject.error_message);
		
		// just leave game
		return ProtocolState.LEAVE_GAME;
	}


	/**
	 * Sent by a host to each player after connection as players join the game. 
	 * Maps player IDs to real names. Optional command, 
	 * will only be sent if the player specified a real name itself.
	 * @return state change 
	 */
	private ProtocolState players_joined(String command){	
		String received = client.receive();
		
		// got it from host to be informed about players
		players_joined players = (players_joined) Jsonify.getJsonStringAsObject(received, players_joined.class);
			
		if(players != null){
			startingPlayers.clear();
			interfaceMapping.clear();
			
			// creating all players specified by the protocol
			String[][] playersDetails = players.players;
			PlayerInterface playersInt;
			Player player;
			for(String[] details : playersDetails){
				playersInt = new RemotePlayer();
				player = new Player(playersInt, 0, Integer.parseInt(details[0]), details[1]);
		
				// add the player to the mapping of players
				// and their interfaces for future use
				interfaceMapping.put(player.getNumberId(), playersInt);
				startingPlayers.add(player);		
			}
			return ProtocolState.PLAYERS_JOINED;
		}
		
		else
			return ping(received);
		
	}


	//from now on all messages must be broadcast to all clients
	private ProtocolState ping(String command){
		
		ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);
		if(ping == null)
			return ProtocolState.LEAVE_GAME;
		
		if(state.getPlayers().size() == 0){
			// to be changed in the future (maybe)
			state.setPlayers(startingPlayers);
		}
		
		//	technically ask in UI: do you still want to play?
		ping response = new ping(ping.no_of_players, myID);
		client.send(Jsonify.getObjectAsJsonString(response));
		
		// ignore other pings (no need to check for them - the host will tell
		// you if someone timed out)
		return ProtocolState.TIMEOUT;
	}
	
	
	private ProtocolState timeout(String command){
		String received = client.receive();
		
		if(received.contains("ping"))
			return ProtocolState.TIMEOUT;
		if(received.contains("ready"))
			return ready(received);
		
		//TODO: game might be ended here... add this condition!
		
		timeout timeout = (timeout) Jsonify.getJsonStringAsObject(received, timeout.class);
		if(timeout == null)
			return ProtocolState.LEAVE_GAME;
		
		int playerOut = timeout.payload;
		remove_player(playerOut);
		
		// keep waiting for other timeouts
		return ProtocolState.TIMEOUT;
		
	}
	
	
	protected void remove_player(int id){
		Player player = state.lookUpPlayer(id);
		
		// if a game hasnt started and a player needs to be removed
		// then the starting array should be altered
		if(engine == null){
			startingPlayers.remove(state.lookUpPlayer(id));
		}
		
		// TODO: make sure its ok with game engine
		PlayerUtils.removePlayer(state, player);
	}
	
	


	private ProtocolState ready(String command){
		
		ready ready = (ready) Jsonify.getJsonStringAsObject(command, ready.class);
		if(ready == null){
			System.out.println("Something went horribly wrong with Json");
			return ProtocolState.LEAVE_GAME;
		}
		
		acknowledgement ack = new acknowledgement(1, myID);
		client.send(Jsonify.getObjectAsJsonString(ack));
		
		return ProtocolState.INIT_GAME;
	}

	
	private ProtocolState init_game(String command){
		String received = client.receive();
		initalise_game init = (initalise_game) Jsonify.getJsonStringAsObject(received, initalise_game.class);
		
		if(init == null)
			return ProtocolState.LEAVE_GAME;
		
		versionPlayed = init.version;
		featuresUsed = init.supported_features;
		
		return ProtocolState.SETUP_GAME;
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


	private ProtocolState leave_game(String command){
		return protocolState;

	}
	
	
	private void remove_player(PeerConnection connection){
		int id = connection.id;
		Player player = state.lookUpPlayer(id);
		
		// TODO: make sure its ok with game engine
		PlayerUtils.removePlayer(state, player);
		
		// remove that player from connections etc
	//	connections.remove(connection);
	//	idMap.remove(id);
		
		// if a game hasnt started and a player needs to be removed
		// then the starting array should be altered
		if(engine == null){
			startingPlayers.remove(state.lookUpPlayer(id));
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
		ClientProtocol protocol = new ClientProtocol();
		protocol.state = new State();
		RiskMapGameBuilder.addRiskTerritoriesToState(protocol.state);
		
		// to send receive messages etc
		// localhost should be replaced with an argument args[0], port args[1]
		protocol.client = new Client("localhost", 4444);
		
		String[] names = new String[]{"Chappie", "Rex", "Monkey", "XXX",
				"Gandalf", "Pinguin", "Chocolate", "Billy", "Panda", "Zebra",
				"Billy the Pinguin", "Mike the Pistacio", "The Machine", "Unknown",
				"RISK Master"};

		protocol.funNames.addAll(Arrays.asList(names));
		
		while(true){
			protocol.handleCommand("");
		}
		// loop somehow...
		
		
	}

	
}
