package PeerServer.server;

import static com.esotericsoftware.minlog.Log.debug;
import GameBuilders.DemoGameBuilder;
import GameBuilders.RiskMapGameBuilder;
import GameEngine.ArbitrationAbstract;
import GameEngine.GameEngine;
import GameEngine.NetworkArbitration;
import GameEngine.PlayState;
import GameEngine.RequestReason;
import GameState.Player;
import GameState.State;
import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.Results.Change;
import GeneralUtils.Jsonify;
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
import PeerServer.protocol.gameplay.deploy;
import PeerServer.protocol.gameplay.fortify;
import PeerServer.protocol.gameplay.setup;
import PeerServer.protocol.general.acknowledgement;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import PlayerInput.RemotePlayer;
import PlayerInput.MyEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class AbstractProtocol implements Runnable {

	protected int ack_timeout = 2;
	protected int move_timeout = 2;
	protected ProtocolState protocolState = ProtocolState.JOIN_GAME;
	protected String errorMessage = "default";
	protected ArrayList<String> names = new ArrayList<String>();
	protected ArrayList<String> funNames = new ArrayList<String>();
	protected int ack_id = 0;
	protected int their_ack_id = 0;
	protected int myID;
	protected String myName;
	
 // GAME ENGINE RELATED
	protected State state;
	protected GameEngine engine = null;
	protected ArrayList<Player> startingPlayers = new ArrayList<Player>();
	protected int numOfPlayers;
	protected int firstPlayerId = -1;
	protected int currentPlayerId = -1;
	protected NetworkArbitration networkArbitration = new NetworkArbitration();
	protected Thread gameEngineThread;

	// maps a player id with a blocking queue to notify them about their responses
	// or take response from them (if they are local players)
	protected HashMap<Integer, BlockingQueue<Entry<Integer, RequestReason>>> queueMapping =
			new HashMap<Integer, BlockingQueue<Entry<Integer, RequestReason>>>();
	protected BlockingQueue<Object> localPlayersQueue = new LinkedBlockingQueue<Object>();
	
// DIE ROLLS
//TODO: need to change the amount of faces accordingly!
	protected Die diceRoller;
	protected RandomNumbers randGenerator;
	protected byte[] randomNumber;
	protected int dieRollResult;


// TIMER THINGS
	protected Timer timer = new Timer();
	protected ChangeState currentTask;
	protected Thread mainThread = Thread.currentThread();
	protected boolean timerSet;
	public boolean wrongCommand = false;
	protected ProtocolState nextStateAfterAck;
	
// ABSTRACT METHODS
	protected abstract void takeSetupAction();
	protected abstract void sendCommand(String command, Integer exceptId, boolean ack);
	protected abstract String receiveCommand();
	protected abstract void sendLeaveGame(int code, String reason);
	protected abstract void handleLeaveGame(String command);
	protected abstract void acknowledge(int ack);

	public void run(){
		try {
			diceRoller = new Die();
		} catch (HashMismatchException e) {
			e.printStackTrace();
		}

		//RiskMapGameBuilder.addRiskTerritoriesToState(state);
		DemoGameBuilder.addFourTerritories(state);
		
		while(protocolState != null){
			if(engine == null)
				takeSetupAction();
			else 
				takeGameAction();
		}
		
		System.out.println("end");
	}


	/**
	 * Manages the different states and associated commands.
	 * @param protocol_command
	 * @return
	 */
	protected void takeGameAction() {
		if(Thread.interrupted()){
			if(timerSet){
				synchronized(currentTask){
					try {
						currentTask.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else{
				if(wrongCommand)
					sendLeaveGame(200, "Wrong commad received.");
			}
		}
		
		if(currentPlayerId  == -1)
			currentPlayerId = 0;
        
        switch (protocolState) {
	     //   case ROLL:
	     //       debug("\n ROLL");
	     //       ArrayList<Integer> diceRolls;
	     //       if(engine == null)
	     //       	diceRolls = roll_hash(0);
	     //       else
	    //        	diceRolls = roll_hash(0); // dont know :((
	       case SETUP_GAME:
	    	   debug("\n SETUP_GAME");
//	    	   networkArbitration.setFirstPlayerId(firstPlayerId); // this should be got by now
	    	   if(gameEngineThread == null){
		    	   gameEngineThread = new Thread(engine);
		    	   gameEngineThread.start();
		    	   currentPlayerId = engine.getState().getPlayerQueue().getCurrent().getNumberId();
	    	   }
	    	   setupGame();
			   break;
        /*    case PLAY_CARDS:
	             debug("\n PLAY_CARDS");
	            playCards();
	            break;
			*/	
            case DEPLOY:
				debug("\n DEPLOY");
				deploy();
				
				break;
            case ATTACK:
            	debug("\n ATTACK");
				attack();
	            break;
            case DEFEND:
				debug("\n DEFEND");
				defend();
				break;
            case ATTACK_CAPTURE:
				debug("\n ATTACK_CAPTURE");
				attackCapture();
	            break;
            case FORTIFY:
				debug("\n FORTIFY");
				fortify();	     
	            break;
            default:
			//	System.out.println("in default not good");
				break;

		}
	}


// ===============================================================
//  MAIN GAME COMMANDS
// ===============================================================
	
	protected void setupGame(){
		nextStateAfterAck = ProtocolState.SETUP_GAME;
		boolean changeToDeploy = false;	
		
		if(currentPlayerId == myID){
			setup setup = (setup) getResponseFromLocalPlayer();
			setup.ack_id = ack_id;
			ack_id++;
			
			String setupString = Jsonify.getObjectAsJsonString(setup);
			//Send to all clients
			sendCommand(setupString, null, true);
		}
		//someone sent us a command
		else {
			String command = receiveCommand();
			setup setup = (setup) Jsonify.getJsonStringAsObject(command, setup.class);

			if(setup == null){
				sendLeaveGame(200, "Wrong command. Expected setup.");
				return;
			}
			
			notifyPlayerInterface(setup, setup.player_id);
			their_ack_id = setup.ack_id;
			
		 	sendCommand(command, setup.player_id, true);
		 	acknowledge(setup.ack_id);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(engine.getPlayState());
		Change lastChange = engine.getStateChangeRecord().getLastChange();
		if(lastChange != null){
			String lastPlayer = lastChange.getActingPlayerId();
			PlayState changeType = lastChange.getActionPlayed();

			if((engine.getPlayState() != PlayState.USING_REMAINING_ARMIES) && (engine.getPlayState() != PlayState.FILLING_EMPTY_COUNTRIES)){
				if(currentPlayerId == myID){
					System.out.println("CHANGE to deploy case 1");
					protocolState = ProtocolState.DEPLOY;
				}
				else if((lastPlayer.equals(myName)) && (changeType.name().equals(PlayState.USING_REMAINING_ARMIES.name()))){
				  protocolState = ProtocolState.SETUP_GAME;
				 }
				else {
					System.out.println("CHANGE to deploy Case 2");
					System.out.println(lastPlayer + "  " + changeType.name() + " .... my name: " + myName);
					protocolState = ProtocolState.DEPLOY;
				}
			}
		}

		currentPlayerId = (currentPlayerId + 1) % numOfPlayers;
	
	}
	

	protected void playCards(){
		/*if(currentPlayerId == myID){
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
			}
			//send command to all players execpt the one who sent it to us
			sendCommand(command, pc.player_id);
			//notify player interface to update game state/engine 
			notifyPlayerInterface(pc, pc.player_id);
		}*/
	}

	
	
	protected void deploy(){
		System.out.println("PROTOCOL: DEPLOY\n");
		nextStateAfterAck = ProtocolState.ATTACK;

		//we are sending command 
		if(currentPlayerId == myID){
			//create deploy object based on player choices
			deploy deploy = (deploy) getResponseFromLocalPlayer();
			String deployString = Jsonify.getObjectAsJsonString(deploy);
			
			//send to all clients or just host if you are client 
			sendCommand(deployString, null, true);
		}
		else{
			String command = receiveCommand();
			deploy deploy = (deploy) Jsonify.getJsonStringAsObject(command, deploy.class);
			if(deploy == null){
			}
			notifyPlayerInterface(deploy, deploy.player_id);
			their_ack_id = deploy.ack_id;
			
			sendCommand(command, deploy.player_id, true);
			acknowledge(their_ack_id);
		}
		protocolState = ProtocolState.ATTACK;
	}


	protected void attack(){
		System.out.println("PROTOCOL: ATTACK\n");
		//we are sending command 
		if(currentPlayerId == myID){		
			attack attack = (attack) getResponseFromLocalPlayer();
			String attackString = Jsonify.getObjectAsJsonString(attack);
			
			if(attack.payload == null)
				nextStateAfterAck = ProtocolState.FORTIFY;
			else
				nextStateAfterAck = ProtocolState.DEFEND;
			
			sendCommand(attackString, null, true);
		}
		//someone sent us the command  player id if parsing
		else{
			String command = receiveCommand();
			attack attack = (attack) Jsonify.getJsonStringAsObject(command, attack.class);
			//check its not null
			if(attack == null){
			}
			
			notifyPlayerInterface(attack, attack.player_id);

			their_ack_id = attack.ack_id;

			if(attack.payload == null)
				nextStateAfterAck = ProtocolState.FORTIFY;
			else
				nextStateAfterAck = ProtocolState.DEFEND;
			
			sendCommand(command, attack.player_id, true);
			acknowledge(their_ack_id);
			// TODO: dice roll?
		}
		protocolState = nextStateAfterAck;
		if(protocolState == ProtocolState.DEFEND){
			System.out.println("change player " + (currentPlayerId + 1));
			currentPlayerId = (currentPlayerId + 1) % numOfPlayers;
		}
	}

	protected void defend(){
		System.out.println("PROTOCOL: DEFEND\n");
		//we are sending command 
		nextStateAfterAck = ProtocolState.ATTACK_CAPTURE;
		if(currentPlayerId == myID){
			defend defend = (defend) getResponseFromLocalPlayer();
			String defendString = Jsonify.getObjectAsJsonString(defend);

			sendCommand(defendString, null, true);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			String command = receiveCommand();
			defend defend = (defend) Jsonify.getJsonStringAsObject(command, defend.class);
			if(defend == null){
			}

			notifyPlayerInterface(defend, defend.player_id);
			their_ack_id = defend.ack_id;
		
			sendCommand(command, defend.player_id, true);
			acknowledge(their_ack_id);	
		}
		protocolState = nextStateAfterAck;
		currentPlayerId = (currentPlayerId + 1) % numOfPlayers;
	}

	protected void attackCapture(){
		System.out.println("PROTOCOL: ATTACK_CAPTURE\n");

		nextStateAfterAck = ProtocolState.ATTACK;
		
		// TODO: check whether we actually captured a territory?
		if(engine.isCountryTaken()){
			engine.resetCountryTaken();
			
			if(currentPlayerId == myID){
				attack_capture attack_capture = (attack_capture) getResponseFromLocalPlayer();
				String attack_captureString = Jsonify.getObjectAsJsonString(attack_capture);
				
				sendCommand(attack_captureString, null, true);
			}
			//someone sent us the command  player id if parsing
			else{
				String command = receiveCommand();
				//parse command into deploy object
				attack_capture attack_capture = (attack_capture) Jsonify.getJsonStringAsObject(command, 
						attack_capture.class);
	
				//check its not null
				if(attack_capture == null){
				}
				
				notifyPlayerInterface(attack_capture, attack_capture.player_id);
				their_ack_id = attack_capture.ack_id;
				sendCommand(command, attack_capture.player_id, true);
				acknowledge(their_ack_id);
			}
		}

		protocolState = nextStateAfterAck;
	}


	protected void fortify(){
		System.out.println("PROTOCOL: FORTIFY\n");
		System.out.println(currentPlayerId);
		nextStateAfterAck = ProtocolState.DEPLOY; // TODO: change this
		if(currentPlayerId == myID){
			fortify fortify = (fortify) getResponseFromLocalPlayer();
			String fortifyString = Jsonify.getObjectAsJsonString(fortify);

			sendCommand(fortifyString, null, true);
		}
		//someone sent us the command  player id if parsing
		else{
			String command = receiveCommand();
			fortify fortify = (fortify) Jsonify.getJsonStringAsObject(command, 
					fortify.class);
			if(fortify == null){
			}

			notifyPlayerInterface(fortify, fortify.player_id);
			their_ack_id = fortify.ack_id;
			sendCommand(command, fortify.player_id, true);
			acknowledge(their_ack_id);
		}
		protocolState = nextStateAfterAck;
		currentPlayerId = (currentPlayerId + 1) % numOfPlayers;
	}

	//*********************** DICE ROLLS ******************************

	/**
	 * @param protocol_command
	 * @return
	 */
	protected ArrayList<Integer> roll_hash(int faces){
		return null;
		/*// we are sending command
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
			}

		}
		// sb sent us command
		else{
			roll_hash rh = (roll_hash) Jsonify.getJsonStringAsObject(command, roll_hash.class);
			if(rh == null){
			}

			String hash = rh.payload;
			int player_id = rh.player_id;

			// add hash to mapping of hashes and player ids
			try {
				diceRoller.addHash(player_id, hash);
			} catch (HashMismatchException e) {
				e.printStackTrace();
			}

			// if its a server it will send to everyone except the person that sent it
			// if its a client it will ignore it
			sendCommand(command, player_id);
		}
*/
	//	if(diceRoller.getNumberOfReceivedHashes() == numOfPlayers)
	//		return ProtocolState.ROLL_NUMBER;
	}

	
	protected void roll_number(){
/*
		// we are sendin roll_number
		if(command == ""){
			System.out.println(randomNumber.toString());
			String ranNumStr = new String(randomNumber);
			roll_number rn = new roll_number(ranNumStr, myID);

			try {
				diceRoller.addNumber(myID, ranNumStr);
			} catch (HashMismatchException e) {
				e.printStackTrace();
			}

			String rnStr = Jsonify.getObjectAsJsonString(rn);
			sendCommand(rnStr, null);
		}
		// we got roll number, need to check it
		else{
			roll_number rn = (roll_number) Jsonify.getJsonStringAsObject(command, roll_number.class);
			if(rn == null){
				
			}


			String number = rn.payload;
			int id = rn.player_id;

			try {
				diceRoller.addNumber(id, number);
			} catch (HashMismatchException e) {
				e.printStackTrace();
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
			} catch (OutOfEntropyException e) {
				e.printStackTrace();
			}
		}*/
	}
	

// ===============================================================
//  CREATING AND REMOVING PLAYERS
// ===============================================================

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
	
		PlayerInterface playerInterface;
		if(localPlayer){
			BlockingQueue<Object> newSharedQueue = new LinkedBlockingQueue<Object>();
			playerInterface = new DumbBotInterface(newSharedQueue, id);
			localPlayersQueue = newSharedQueue;
		}
		else{	
			BlockingQueue<Entry<Integer, RequestReason>> newSharedQueue = new LinkedBlockingQueue<Entry<Integer, RequestReason>>();
			playerInterface = new RemotePlayer(newSharedQueue, Thread.currentThread(), this);
			queueMapping.put(id, newSharedQueue); // the mappigng stores all interfaces
		}

	
		Player newOne;
		if(name != "")
			newOne = new Player(playerInterface, id, name);
		else
			newOne = new Player(playerInterface, id);

		startingPlayers.add(newOne);
		numOfPlayers++;
		
		//interfaceMapping.put(0, playerInterface);
		if(state != null)
			state.setPlayers(startingPlayers); /// ???? needed?
		
		return newOne;
	}

	
// ===============================================================
//  COMMUNICATION WITH PLAYER INTERFACES
// ===============================================================
	
	/**
	 * Method used to contact the PlayerInterface (RemotePlayer) and notify it
	 * about its new response
     * @param response
     * @param playerId
     */
	protected void notifyPlayerInterface(Object response, Integer playerId){
		BlockingQueue<Entry<Integer, RequestReason>> queue = queueMapping.get(playerId);

		if(response instanceof setup){
			try {
				queue.put(new MyEntry(((setup) response).payload, RequestReason.PLACING_ARMIES_SET_UP));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(response instanceof attack){
			attack attack = (attack) response;
			try {
				if(attack.payload == null){
					queue.put(new MyEntry(null, RequestReason.ATTACK_CHOICE_FROM));
				}
				else{
					queue.put(new MyEntry(attack.payload[0], RequestReason.ATTACK_CHOICE_FROM));
					queue.put(new MyEntry(attack.payload[1], RequestReason.ATTACK_CHOICE_TO));
					queue.put(new MyEntry(attack.payload[2], RequestReason.ATTACK_CHOICE_DICE));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		else if(response instanceof defend){
			try {
				queue.put(new MyEntry(((defend) response).payload, RequestReason.DEFEND_CHOICE_DICE));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		else if(response instanceof deploy){
			deploy deploy = (deploy) response;
			int[][] pairs = deploy.payload.pairs;
			for(int i = 0; i < pairs.length; i++){
				try {
					queue.put(new MyEntry(pairs[i][0], RequestReason.PLACING_ARMIES_PHASE));
					queue.put(new MyEntry(pairs[i][1], RequestReason.PLACING_ARMIES_PHASE));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
		}
		else if(response instanceof fortify){
			fortify fortify = (fortify) response;
			try {
				if(fortify.payload == null){
					queue.put(new MyEntry(null, RequestReason.REINFORCEMENT_PHASE));
				}
				else{
					queue.put(new MyEntry(fortify.payload[0], RequestReason.REINFORCEMENT_PHASE));
					queue.put(new MyEntry(fortify.payload[1], RequestReason.REINFORCEMENT_PHASE));
					queue.put(new MyEntry(fortify.payload[2], RequestReason.REINFORCEMENT_PHASE));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(response instanceof attack_capture){
			attack_capture attack_capture = (attack_capture) response;
			try {
				queue.put(new MyEntry(attack_capture.payload[2], RequestReason.POST_ATTACK_MOVEMENT));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Gets a response from the local player (their move, parsed into protocol command
	 * object)
	 * @return
	 */
	protected Object getResponseFromLocalPlayer(){
		Object response = null;
		try {
			response = localPlayersQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	
// ===============================================================
//  NAME CREATION
// ===============================================================	
	
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
	 * ChangeState represents a thread that is supposed
	 * to run after a time specified in a timer which uses it
	 * The thread simply changes the state of the game to the next one
	 *
	 */
	public class ChangeState extends TimerTask{
		private ProtocolState nextState;
		
		public ChangeState(ProtocolState next){
			nextState = next;
			timerSet = true;
		}
		
		@Override
		public void run() {
			myID = 0;

			// interrupt the main thread - for safety, so it doesn't access the protocolState
			// field we want to change and to make sure its not in the middle of sth that
			// we can mess up by changing state
			mainThread.interrupt();
			synchronized(this){
				try {
					// wait for a bit  to let the main thread reach interrupt
					wait(4000);
				} catch (Exception e) {}

				// change state to the following state
				protocolState = nextState;
				// change state and notify the main thread so it continues running
				timerSet = false;
				notify();
			}
		}		
	}
	
	


}
