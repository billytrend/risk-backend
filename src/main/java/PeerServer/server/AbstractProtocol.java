package PeerServer.server;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;
import PeerServer.protocol.cards.deploy;
import PeerServer.protocol.cards.play_cards;
import PeerServer.protocol.dice.Die;
import PeerServer.protocol.dice.Die.HashMismatchException;
import PeerServer.protocol.dice.Die.OutOfEntropyException;
import PeerServer.protocol.dice.RandomNumbers;
import PeerServer.protocol.dice.roll_hash;
import PeerServer.protocol.dice.roll_number;
import PeerServer.protocol.gameplay.attack;
import PeerServer.protocol.gameplay.attack_capture;
import PeerServer.protocol.gameplay.defend;
import PeerServer.protocol.gameplay.fortify;
import PeerServer.protocol.general.acknowledgement;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import PlayerInput.RemotePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class AbstractProtocol implements Runnable {

	// TODO: in seconds for now: should they be?
	protected int ack_timeout = 3;
	protected int move_timeout = 3;
	protected ProtocolState protocolState = ProtocolState.JOIN_GAME;
	protected String errorMessage = "default";

	protected State state;
	protected ArrayList<Player> startingPlayers = new ArrayList<Player>();
	//protected HashMap<Integer, PlayerInterface> interfaceMapping = new HashMap<Integer, PlayerInterface>();
	
	// maps a player id with a blocking queue to notify them about their responses
	// or take response from them (if they are local players)
	protected HashMap<Integer, BlockingQueue<Object>> queueMapping = new HashMap<Integer, BlockingQueue<Object>>();
	protected ArrayList<String> names = new ArrayList<String>();
	protected ArrayList<String> funNames = new ArrayList<String>();

	protected String leaveReason;
	protected int leaveCode;
	protected PlayerInterface localPlayer;

	protected GameEngine engine = null;
	protected int ack_id = 0;
	protected int myID;

	//TODO: need to change the amount of faces accordingly!
	protected Die diceRoller;
	protected RandomNumbers randGenerator;
	protected byte[] randomNumber;
	protected int dieRollResult;

	protected int numOfPlayers;

	public void run(){
		state = new State();

		try {
			diceRoller = new Die();
		} catch (HashMismatchException e) {
			e.printStackTrace();
		}

		RiskMapGameBuilder.addRiskTerritoriesToState(state);

		while(protocolState != null){
			if(engine == null)
				takeSetupAction();
			else 
				takeGameAction();
		}
		System.out.println("end");
	}

	protected abstract void takeSetupAction();

	protected abstract void sendCommand(String command, Integer exceptId);
	protected abstract String receiveCommand();

	/**
	 * Manages the different states and associated commands.
	 * @param command
	 * @return
	 */
	protected abstract void takeGameAction();


	/**
	 * Removes the player from the state and the array of starting players
	 * if the game has not started yet
	 * @param id
	 */
	protected void removePlayer(int id){
		Player player = state.lookUpPlayer(id);

		numOfPlayers--;
		// if a game hasnt started and a player needs to be removed
		// then the starting array should be altered
		if(engine == null){
			startingPlayers.remove(state.lookUpPlayer(id));
		}

		// remove them from interface mapping
		//	interfaceMapping.remove(id);
		queueMapping.remove(id);

		// TODO: make sure its ok with game engine
		PlayerUtils.removePlayer(state, player);
	}

	protected Player createNewPlayer(String name, int id, boolean localPlayer){
		// creating player and mapping its id to its interface
		BlockingQueue<Object> newSharedQueue = new LinkedBlockingQueue<Object>();
		queueMapping.put(id, newSharedQueue); // the mappigng stores all interfaces
												// even local - need for checks!
		
		PlayerInterface playerInterface;
		if(localPlayer){
			playerInterface = new DumbBotInterface(newSharedQueue, id);
			this.localPlayer = playerInterface;
		}
		else
			playerInterface = new RemotePlayer(newSharedQueue);

	
		Player newOne;
		if(name != "")
			newOne = new Player(playerInterface, id, name);
		else
			newOne = new Player(playerInterface, id);

		startingPlayers.add(newOne);
		numOfPlayers++;
		
		//interfaceMapping.put(0, playerInterface);
		state.setPlayers(startingPlayers); /// ???? needed?
		
		return newOne;
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

	/**
	 * Create fun names to use in the game!
	 */
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
     * @param response
     * @param playerId
     */
	protected void notifyPlayerInterface(Object response, Integer playerId){
		BlockingQueue<Object> queue = queueMapping.get(playerId);
		try {
			queue.put(response);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a response from the local player (their move, parsed into protocol command
	 * object)
	 * @return
	 */
	protected Object getResponseFromLocalPlayer(){
		BlockingQueue<Object> queue = queueMapping.get(myID);
		localPlayer.createResponse();
		
		Object response = null;
		try {
			response = queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
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
     * @param errorMessage2
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

	protected abstract ProtocolState ready(String command);

	protected abstract ProtocolState acknowledge(String command);

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
		//we are sending command 
		if(command == ""){
			//create play_cards object 
			play_cards pc = (PeerServer.protocol.cards.play_cards) getResponseFromLocalPlayer();
			String playCardsString = Jsonify.getObjectAsJsonString(pc);
			sendCommand(playCardsString, null);
		}
		//someone sent us a command 
		else {
			//parse comamnd into play_cards object
			play_cards pc = (PeerServer.protocol.cards.play_cards) Jsonify.getJsonStringAsObject(command, play_cards.class);
			
			if(pc == null){
				return protocolState.LEAVE_GAME;
			}
			//send command to all players execpt the one who sent it to us
			sendCommand(command, pc.player_id);
			//notify player interface to update game state/engine 
			notifyPlayerInterface(pc, pc.player_id);
		}

		return protocolState.ACK;	
	}

	protected ProtocolState deploy(String command){
		//we are sending command 
		if(command == ""){
			//create deploy object based on player choices
			deploy deploy = (PeerServer.protocol.cards.deploy) getResponseFromLocalPlayer();
			String deployString = Jsonify.getObjectAsJsonString(deploy);
			
			//send to all clients or just host if you are client 
			sendCommand(deployString, null);
		}
		//someone sent us the command
		else{
			//parse command into deploy object
			deploy deploy = (deploy) Jsonify.getJsonStringAsObject(command, deploy.class);
			
			//check its not null
			if(deploy == null){
				return protocolState.LEAVE_GAME;
			}
			//send command to all players execpt the one who sent it to us
			sendCommand(command, deploy.player_id);
			//notify player interface to update game state/engine 
			notifyPlayerInterface(deploy, deploy.player_id);
		}

		//ack required
		return protocolState.ACK;	
	}


	//*********************** ATTACK / DEFEND ******************************

	protected ProtocolState attack(String command) {
		//we are sending command 
		if(command == ""){
			//create deploy object based on player choices
			attack attack = (attack) getResponseFromLocalPlayer();
			
			//transfer object to JSON string
			String attackString = Jsonify.getObjectAsJsonString(attack);
			
			//send to all clients or just host if you are client 
			sendCommand(attackString, null);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			attack attack = (attack) Jsonify.getJsonStringAsObject(command, attack.class);
			//check its not null
			if(attack == null){
				return protocolState.LEAVE_GAME;
			}

			sendCommand(command, attack.player_id);
			//notify interface
			notifyPlayerInterface(attack, attack.player_id);

            //Call dice roll


		}

		//might not be to ACK
		return protocolState.ACK;	
	}

	protected ProtocolState defend(String command){
		//we are sending command 
		if(command == ""){
			//create deploy object based on player choices
			defend defend = (defend) getResponseFromLocalPlayer();
			//transfer object to JSON string
			String defendString = Jsonify.getObjectAsJsonString(defend);
			//send to all clients or just host if you are client 
			sendCommand(defendString, null);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			defend defend = (defend) Jsonify.getJsonStringAsObject(command, defend.class);

			//check its not null
			if(defend == null){
				return protocolState.LEAVE_GAME;
			}

			sendCommand(command, defend.player_id);
			//notify interface
			notifyPlayerInterface(defend, defend.player_id);
		}

		return protocolState.ACK;

	}

	protected ProtocolState attack_capture(String command){
		//we are sending command 
		if(command == ""){
			//create deploy object based on player choices
			attack_capture attack_capture = (attack_capture) getResponseFromLocalPlayer();
			//transfer object to JSON string
			String attack_captureString = Jsonify.getObjectAsJsonString(attack_capture);
			//send to all clients or just host if you are client 
			sendCommand(attack_captureString, null);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			attack_capture attack_capture = (attack_capture) Jsonify.getJsonStringAsObject(command, 
					attack_capture.class);

			//check its not null
			if(attack_capture == null){
				return protocolState.LEAVE_GAME;
			}

			sendCommand(command, attack_capture.player_id);
			//notify interface
			notifyPlayerInterface(attack_capture, attack_capture.player_id);
		}

		return protocolState.ACK;
	}


	protected ProtocolState fortify(String command){
		//we are sending command 
		if(command == ""){
			//create deploy object based on player choices
			fortify fortify = (fortify) getResponseFromLocalPlayer();
			//transfer object to JSON string
			String fortifyString = Jsonify.getObjectAsJsonString(fortify);
			//send to all clients or just host if you are client 
			sendCommand(fortifyString, null);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			fortify fortify = (fortify) Jsonify.getJsonStringAsObject(command, 
					fortify.class);

			//check its not null
			if(fortify == null){
				return protocolState.LEAVE_GAME;
			}

			sendCommand(command, fortify.player_id);
			//notify interface
			notifyPlayerInterface(fortify, fortify.player_id);
		}

		return protocolState.ACK;
	}

	protected ProtocolState ack(String command){
		//we are sending command 
		if(command == ""){
			//create deploy object based on player choices
			acknowledgement acknowledgement = (acknowledgement) getResponseFromLocalPlayer();
			//transfer object to JSON string
			String acknowledgementString = Jsonify.getObjectAsJsonString(acknowledgement);
			//send to all clients or just host if you are client 
			sendCommand(acknowledgementString, null);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			acknowledgement acknowledgement = (acknowledgement) Jsonify.getJsonStringAsObject(command, 
					acknowledgement.class);

			//check its not null
			if(acknowledgement == null){
				return protocolState.LEAVE_GAME;
			}
		}

		return protocolState.ACK;
	}

	//*********************** DICE ROLLS ******************************

	/**
	 * @param command
	 * @return
	 */
	protected ProtocolState roll_hash(String command){
		// we are sending command
		if(command == ""){			//empty your sending 
			randomNumber = diceRoller.generateNumber();
			try {
				byte[] hash = diceRoller.hashByteArr(randomNumber);
				String hashStr = new String(hash);

				roll_hash rh = new roll_hash(hashStr, myID);
				diceRoller.addHash(myID, hashStr);

				// if its a client it will send to host, if its a host it will send to all
				sendCommand(Jsonify.getObjectAsJsonString(rh), null);

			} catch (HashMismatchException e) {
				e.printStackTrace();
				return ProtocolState.LEAVE_GAME;
			}

		}
		// sb sent us command
		else{
			roll_hash rh = (roll_hash) Jsonify.getJsonStringAsObject(command, roll_hash.class);
			if(rh == null){
				return ProtocolState.LEAVE_GAME;
			}

			String hash = rh.payload;
			int player_id = rh.player_id;

			// add hash to mapping of hashes and player ids
			try {
				diceRoller.addHash(player_id, hash);
			} catch (HashMismatchException e) {
				e.printStackTrace();
				return ProtocolState.LEAVE_GAME;
			}

			// if its a server it will send to everyone except the person that sent it
			// if its a client it will ignore it
			sendCommand(command, player_id);
		}

		if(diceRoller.getNumberOfReceivedHashes() == numOfPlayers)
			return ProtocolState.ROLL_NUMBER;

		return ProtocolState.ROLL_HASH;
	}

	protected ProtocolState roll_number(String command){

		// we are sendin roll_number
		if(command == ""){
			System.out.println(randomNumber.toString());
			String ranNumStr = new String(randomNumber);
			roll_number rn = new roll_number(ranNumStr, myID);

			try {
				diceRoller.addNumber(myID, ranNumStr);
			} catch (HashMismatchException e) {
				e.printStackTrace();
				return ProtocolState.LEAVE_GAME;
			}

			String rnStr = Jsonify.getObjectAsJsonString(rn);
			sendCommand(rnStr, null);
		}
		// we got roll number, need to check it
		else{
			roll_number rn = (roll_number) Jsonify.getJsonStringAsObject(command, roll_number.class);
			if(rn == null)
				return ProtocolState.LEAVE_GAME;

			String number = rn.payload;
			int id = rn.player_id;

			try {
				diceRoller.addNumber(id, number);
			} catch (HashMismatchException e) {
				e.printStackTrace();
				return ProtocolState.LEAVE_GAME;
			}
		}

		if(diceRoller.getNumberOfReceivedNumbers() == numOfPlayers){
			try {
				byte[] seed = new byte[256];
				for(int i = 0; i < 256; i++){
					seed[i] = (byte) diceRoller.getByte(); //TODO: check this...
				}

				randGenerator = new RandomNumbers(seed);

				dieRollResult = randGenerator.getRandomInt();
				System.out.println("Die roll result: " + dieRollResult);

			} catch (HashMismatchException e) {
				e.printStackTrace();
				return ProtocolState.LEAVE_GAME;
			} catch (OutOfEntropyException e) {
				e.printStackTrace();
				return ProtocolState.LEAVE_GAME;
			}
		}

		return protocolState;
	}

}
