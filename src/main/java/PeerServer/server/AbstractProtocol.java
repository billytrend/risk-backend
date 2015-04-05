package PeerServer.server;

import static com.esotericsoftware.minlog.Log.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import PlayerInput.PlayerInterface;
import GameBuilders.RiskMapGameBuilder;
import GameEngine.*;
import GameState.*;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;
import PeerServer.server.ProtocolState;


public abstract class AbstractProtocol implements Runnable {

	protected int ack_timeout = 10;
	protected int move_timeout = 10;
	protected ProtocolState protocolState = ProtocolState.JOIN_GAME;
	protected String errorMessage = "default";
	
	protected State state;
	protected ArrayList<Player> startingPlayers = new ArrayList<Player>();
	protected HashMap<Integer, PlayerInterface> interfaceMapping = new HashMap<Integer, PlayerInterface>();
	protected ArrayList<String> names = new ArrayList<String>();
	protected ArrayList<String> funNames = new ArrayList<String>();
	
	protected String leaveReason;
	protected int leaveCode;
	protected PlayerInterface localPlayer;
	
	protected GameEngine engine = null;
	
	/**
	 * Manages the different states and associated commands.
	 * @param command
	 * @return
	 */
	protected abstract void handleSetupCommand(String command);
	
	//protected abstract void sendCommand();
	
	/**
	 * Manages the different states and associated commands.
	 * @param command
	 * @return
	 */
	protected void handleGameCommand(String command){
		switch(this.protocolState){
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
			default:
				System.out.println("IN DEFAULT not good");
				break;
		}
	}

	/**
	 * Removes the player from the state and the array of starting players
	 * if the game has not started yet
	 * @param id
	 */
	protected void removePlayer(int id){
		Player player = state.lookUpPlayer(id);
		
		// if a game hasnt started and a player needs to be removed
		// then the starting array should be altered
		if(engine == null){
			startingPlayers.remove(state.lookUpPlayer(id));
		}
		
		// remove them from interface mapping
		interfaceMapping.remove(id);
		
		// TODO: make sure its ok with game engine
		PlayerUtils.removePlayer(state, player);
	}
	
	
	/**
	 * Choose a name to play with
	 * @return
	 */
	protected String getRandomName(){
		Random ran = new Random();
		if(funNames.size() == 0)
			fillNames();
		return funNames.get(ran.nextInt(funNames.size()));
	}
	
	protected void fillNames(){
		// adding all fun names to be randomly picked as players names
		String[] names = new String[]{"Chappie", "Rex", "Monkey", "XXX",
				"Gandalf", "Pinguin", "Chocolate", "Billy", "Panda", "Zebra",
				"Billy the Pinguin", "Mike the Pistacio", "The Machine", "Unknown",
				"RISK Master"};
		funNames.addAll(Arrays.asList(names));
	}
	
	/**
	 * Method used to contact the PlayerInterface (RemotePlayer) and notify it
	 * about its new response
	 * @param resonse
	 * @param player
	 */
	protected void notifyPlayerInterface(String resonse, PlayerInterface player){
		// TODO:
	}
	
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
	
	//protected abstract ProtocolState ping_ack(String command);
	
	protected abstract ProtocolState ready(String command);
	
	//protected abstract ProtocolState receive_ack(String command);
	
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
