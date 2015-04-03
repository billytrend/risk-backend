package PeerServer.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.accept_join_game;
import PeerServer.protocol.setup.initalise_game;
import PeerServer.protocol.setup.join_game;
import PeerServer.protocol.setup.ping;
import PeerServer.protocol.setup.players_joined;
import PeerServer.protocol.setup.ready;
import PeerServer.protocol.setup.reject_join_game;
import PlayerInput.PlayerInterface;


public class ClientProtocol extends AbstractProtocol{

//	private ArrayList<Integer> acknowlegements; // IDs
	private Client client;
	private Random ran = new Random();
	private int myID;
	private int versionPlayed;
	private String[] featuresUsed;
	private ArrayList<String> funNames = new ArrayList<String>();
	
	
	//*********************** GAME SETUP ******************************

	@Override
	protected ProtocolState join_game(String command) {
		
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
	protected ProtocolState accept_join_game(String command) {
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
	protected ProtocolState reject_join_game(String reason) {
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
	protected ProtocolState players_joined(String command){	
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
	protected ProtocolState ping(String command){
		
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
	
	
	protected ProtocolState timeout(String command){
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
	

	protected ProtocolState ready(String command){
		
		ready ready = (ready) Jsonify.getJsonStringAsObject(command, ready.class);
		if(ready == null){
			System.out.println("Something went horribly wrong with Json");
			return ProtocolState.LEAVE_GAME;
		}
		
		acknowledgement ack = new acknowledgement(1, myID);
		client.send(Jsonify.getObjectAsJsonString(ack));
		
		return ProtocolState.INIT_GAME;
	}

	
	protected ProtocolState init_game(String command){
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
	protected ProtocolState setup_game(String command){
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
	protected ProtocolState play_cards(String command){
		Object play_cards = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.cards.play_cards.class);
		return protocolState;	
	}


	protected ProtocolState draw_card(String command){
		Object draw_card = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.cards.draw_card.class);
		return protocolState;	
	}

	protected ProtocolState deploy(String command){
		Object deploy = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.cards.deploy.class);
		return protocolState;	
	}

	
	protected ProtocolState receive_ack(String command) {
		return null;
	}

	protected ProtocolState ping_ack(String command) {
		return null;
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
