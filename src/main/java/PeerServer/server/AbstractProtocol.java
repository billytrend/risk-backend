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
import PlayerInput.PlayerInterface;
import GameBuilders.RiskMapGameBuilder;
import GameEngine.*;
import GameState.*;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;
import PeerServer.server.ProtocolState;


public abstract class AbstractProtocol {

	protected int ack_timeout;
	protected int move_timeout;
	protected ProtocolState protocolState = ProtocolState.JOIN_GAME;
	protected String errorMessage = "default";
	
	protected State state;
	protected ArrayList<Player> startingPlayers = new ArrayList<Player>();
	protected HashMap<Integer, PlayerInterface> interfaceMapping = new HashMap<Integer, PlayerInterface>();
	protected ArrayList<String> names = new ArrayList<String>();
	
	protected GameEngine engine = null;
	
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

	/**
	 * Removes the player from the state and the array of starting players
	 * if the game has not started yet
	 * @param id
	 */
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
	
	
	

	/**
	 * @param string to be tested
	 * @return true if string is valid JSON
	 * 			false otherwise
	 *
	 * TODO: Do we need this bit?
	 */
/*	public boolean isJsonStringValid(String test) {
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
	*/
	
	/**Jsonify
	 * Sent by a client to a host attempting to join a game. 
	 * First command sent upon opening a socket connection to a host.
	 * @param command 
	 * @return ACCEPT_JOIN_GAME if request successful otherwise REJECT_JOIN_GAME
	 */
	protected abstract ProtocolState join_game(String command);
	
	/**
	 * Sent by a host to a client on receipt of a “join_game” command, 
	 * as confirmation of adding them to the game.
	 * @param command 
	 * @return
	 */
	protected abstract ProtocolState accept_join_game(String command);
	
	
	/**
	 * Sent by a host to a client on receipt of a “join_game” command, as rejection.
	 * @param command
	 * @return END_GAME as the player wont take part in the game
	 */
	protected abstract ProtocolState reject_join_game(String errorMessage2);

	
	/**
	 * Sent by a host to each player after connection as players join the game. 
	 * Maps player IDs to real names. Optional command, 
	 * will only be sent if the player specified a real name itself.
	 * @return state change 
	 */
	protected abstract ProtocolState players_joined(String command);

	protected abstract ProtocolState ping(String command);
	
	protected abstract ProtocolState ping_ack(String command);
	
	protected abstract ProtocolState ready(String command);
	
	protected abstract ProtocolState receive_ack(String command);
	
	protected abstract ProtocolState timeout(String command);
	
	protected abstract ProtocolState init_game(String command);

	protected abstract ProtocolState leave_game(String command);

	/**
	 * Sent by each player in turn at the start of the game to 
	 * claim a territory or reinforce an owned territory (once all have been claimed).
	 * @param command
	 * @return
	 */
	protected abstract ProtocolState setup_game(String command);


	// TODO: Implement all these on the abstract side since both
	// client and host will act very similarly here
	// host will just override these call super() and perform some additional 
	// stuff
	
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

	
	//*********************** ATTACK / DEFEND ******************************

	protected ProtocolState attack(String command) {
		return null;
	}

	protected ProtocolState defend(String command){
		return protocolState;

	}

	protected ProtocolState attack_capture(String command){
		return protocolState;

	}


	protected ProtocolState fortify(String command){
		return protocolState;

	}
	
	protected ProtocolState ack(String command){
		return protocolState;
	}

	//*********************** DICE ROLLS ******************************

	protected ProtocolState roll(String command){
		return protocolState;

	}

	protected ProtocolState roll_hash(String command){
		return protocolState;

	}

	protected ProtocolState roll_number(String command){
		return protocolState;

	}



	
	
	
}
