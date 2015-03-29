package PeerServer.server;

import static PeerServer.server.ProtocolState.*;
import static com.esotericsoftware.minlog.Log.debug;
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
public class Protocol {

	private ProtocolState protocolState = JOIN_GAME;
	private boolean gameInProgress = false;
	
	
	/**
	 * This is the game loop.
	 * Ensures the game runs.
	 */
	private void play() throws InterruptedException {
		while (true) {
			//if(!iterateGame()) return;
		}
	}
	
	/**
	 * Manages the different states and associated commands.
	 * @param command
	 * @return
	 */
	private boolean iterateGame(String command){
		switch(this.protocolState){
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
			break;

		}
		return false;
	}

	/**
	 * Sent by a client to a host attempting to join a game. 
	 * First command sent upon opening a socket connection to a host.
	 * @param command 
	 * @return
	 */
	private ProtocolState join_game(String command) {
		//Checks if the JSON is valid
		if(!isJsonStringValid(command)) return REJECT_JOIN_GAME;
		//create a Java Object matching to the string
		Object join_game = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.join_game.class);
		//pass the results of the objects back to game engine 
		
		//if all is good with GE go to ACCEPT state
		return ACCEPT_JOIN_GAME;
	}
	
	/**
	 *  Sent by a host to a client on receipt of a “join_game” command, 
	 *  as confirmation of adding them to the game.
	 * @param command 
	 * @return
	 */
	private ProtocolState accept_join_game(String command) {
		//Create an accept_join_game object to be sent to client who connected 
		Object accept_join_game = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.accept_join_game.class);
		//move to next state
		return PLAYERS_JOINED;
	}

	/**
	 * Sent by a host to a client on receipt of a “join_game” command, as rejection.
	 * @param command
	 * @return
	 */
	private ProtocolState reject_join_game(String command) {
		//send cute rejection message
		System.out.println("Sorry you cannot join the game \n");
		if(gameInProgress == true) System.out.println("Reason: Game in Progress");
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
		Object players_joined = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.players_joined.class);
		return JOIN_GAME;
	}
	
	
	private ProtocolState ping(String command){
		Object ping = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.ping.class);
		return READY;
	}
	
	private ProtocolState ready(String command){
		Object ping = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.ready.class);
		return READY;
	}

	private ProtocolState init_game(String command){
		Object init_game = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.initalise_game.class);
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
