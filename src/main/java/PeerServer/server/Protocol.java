package PeerServer.server;

import static PeerServer.server.ProtocolState.*;
import static com.esotericsoftware.minlog.Log.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.*;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;
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
	private String[] supported_features;
	private float[] supported_versions;
	private String errorMessage = "default";
	
	private State state;
	private ArrayList<Player> startingPlayers;
	private GameEngine engine = null;
	private TreeMap<Client, String> clientMap = new TreeMap<Client, String>();
	
	
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
			this.protocolState = reject_join_game(command);
			break;
		case PLAYERS_JOINED:
			debug("\n PLAYERS_JOINED");
			this.protocolState = players_joined(command);
			break;
		case PING:
			debug("\n PING");
			this.protocolState = ping(command);
			break;
		case READY:
			debug("\n READY");
			this.protocolState = ready(command);
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
	 * Controls when the game should begin. 
	 * @param command
	 * @return PING if game is launched / WAITING_FOR_PLAYERS if not enough players
	 *
	private ProtocolState waiting_for_players(String command) {
		//ensure the join command is allowed
		try {
			//create JSON object of command
			join_game join_game = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);

			//ensure version set matches game version
			if(join_game.supported_versions != supported_versions){
				return REJECT_JOIN_GAME;
			}
			//ensure features match each other 
			if(join_game.supported_features != supported_features){
				return REJECT_JOIN_GAME;
			}

			
			//  TODO: check whether game is in progress
			
			//reject if game in progress 
			//if(gameInProgress) {
			//	System.out.println("Cant join Reason: Game in Progress!");
			//	errorMessage = "Reason: Game in Progress!";
			//	return REJECT_JOIN_GAME;
			//}
			
			
			
			//reject if no more space in game
			if(PlayerUtils.getPlayersInGame(state).size() == maxNoOfPlayers) {
				System.out.println("Cant join Reason: Game is Full!");
				errorMessage = "Reason: Game is Full!";
				return REJECT_JOIN_GAME;
			}

		}
		catch(Exception error) {
			//if command string does not create a join_game object correctly
			System.out.println("The join_game command was malformed");
			return REJECT_JOIN_GAME;
		}

		//if all above is fine, allow them to join game 
		while(PlayerUtils.getPlayersInGame(state).size() < 3){
			//block until enough players join game 
			System.out.println("STILL WAITING ON MORE PLAYERS");
			//send an accept join game to client
			return ACCEPT_JOIN_GAME;
		}

		//otherwise wait gameLaunch time then launch game
		try {
			this.wait(gameLaunchTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//send ping to all clients
		return PING;
	}
	*/

	
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
			if(engine == null) {
				System.out.println("Cant join Reason: Game in Progress!");
				errorMessage = "Reason: Game in Progress!";
				return REJECT_JOIN_GAME;
			}
			
			//reject if no more space in game
			if(PlayerUtils.getPlayersInGame(state).size() >= maxNoOfPlayers) {
				System.out.println("Cant join Reason: Game is Full!");
				// TODO: send over network
				errorMessage = "Reason: Game is Full!";
				return REJECT_JOIN_GAME;
			}
			
			//send join_game object
			System.out.println("sending... " + join_game);
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

	
	/**
	 * Sent by a host to a client on receipt of a “join_game” command, 
	 * as confirmation of adding them to the game.
	 * @param command 
	 * @return
	 */
	private ProtocolState accept_join_game(String command) {
		//add player's name to players list
		accept_join_game accept_join_game = new accept_join_game(player_id, acknowledgement_timeout, move_timeout)
		
		int id = startingPlayers.size() + 1;
		
		// TODO: starting armies fix
		// TODO: add logic between host being a player
		startingPlayers.add(new Player(new RemotePlayer(), 0, id));
		
		//send this to client
		System.out.println("You have been accepted to the game \n");
		System.out.println("Ack Timeout" + ack_timeout + "\n");
		System.out.println("Move Timeout" + move_timeout + "\n");
		System.out.println("Your PlayerID is: " + id + "\n");
		
		//Create an accept_join_game object
		accept_join_game acg = new accept_join_game(id, ack_timeout, move_timeout);


		//turn object into JSON string to be sent over network 
		String accept_join_game = Jsonify.getObjectAsJsonString(acg);

		//tell the GE we have a new player !ASK BILLY! 
		System.out.println("TELL GAME ENGINE ABOUT NEW PLAYA");
		System.out.println("Sending... " + accept_join_game);

		//if they had a name move to players joined
		return PLAYERS_JOINED;
	}

	/**
	 * Sent by a host to a client on receipt of a “join_game” command, as rejection.
	 * @param command
	 * @return END_GAME as the player wont take part in the game
	 */
	private ProtocolState reject_join_game(String reason) {
		
		reject_join_game reject_join_game = new reject_join_game(errorMessage);
		
		Object rj = Jsonify.getObjectAsJsonString(reject_join_game);
		
		//send rejection 
		System.out.println("Sorry you cannot join the game \n");
		System.out.println(rj);

		//end the game for this thread / player
		return END_GAME;
	}


	/**
	 * Sent by a host to each player after connection as players join the game. 
	 * Maps player IDs to real names. Optional command, 
	 * will only be sent if the player specified a real name itself.
	 * @return state change 
	 */
	private ProtocolState players_joined(String command){
		//send to new player containing current players
		for(int i = 0; i < startingPlayers.size(); i++)
		{
			players_joined new_player_joined = new players_joined(i, playerID[i]);
			Object npj = Jsonify.getObjectAsJsonString(new_player_joined);
			System.out.println(npj);
		}
		
		
		//send to current players including only new player
		players_joined current_player_joined = 
				new players_joined(currentNoOfPlayers, playerID[currentNoOfPlayers]);

		Object cpj = Jsonify.getObjectAsJsonString(current_player_joined);

		//network.send npj	& cpj
		
		//forward to all players
		System.out.println(Arrays.deepToString(players));

		return WAITING_FOR_PLAYERS;
	}


	//from now on all messages must be broadcast to all clients
	private ProtocolState ping(String command){
		System.out.println("Number of players in game: " + currentNoOfPlayers);

		//could potentially start at one if host is not playing
		if(isHost){
			for(int i = 0; i < players.length; i++){
				//create a command with player ID and no of players
				ping ping = new ping(players.length, i);
				Object p = Jsonify.getObjectAsJsonString(ping);
				//send ping to each client
				System.out.println(p);
			}
		}
	
		//need to loop this for each player 
		boolean recievedPing = false;
		//forward that ping to all clients 
		for(int i=0; i < currentNoOfPlayers; i++){
			if(!recievedPing){
				//remove player from the game
				System.out.println("You did not respond within the timeout: You have been kicked!");
				return LEAVE_GAME;
			}
		}

		//if not enough players left terminate game
		if(currentNoOfPlayers < 3){
			System.out.println("End of Game: Sending Leave_Game to all clients");
			return LEAVE_GAME;
		}

		//if all clients respond 
		return READY;
	}

	private ProtocolState ready(String command){
		//send ready command to all connected clients ACK required
		ready ready = new ready();
		
		//block for ack

		//once all acks recieved move to init game
		return INIT_GAME;
	}

	private ProtocolState init_game(String command){
		initalise_game init_game = new initalise_game();
		return READY;
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
