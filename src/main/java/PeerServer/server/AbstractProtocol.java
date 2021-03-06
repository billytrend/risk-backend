package PeerServer.server;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameEngine.NetworkArbitration;
import GameEngine.PlayState;
import GameEngine.RequestReason;
import GameState.Army;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.CardUtils;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;
import GameUtils.Results.Change;
import GeneralUtils.Jsonify;
import PeerServer.protocol.cards.play_cards;
import PeerServer.protocol.dice.SeededGenerator;
import PeerServer.protocol.dice.SeededGenerator.HashMismatchException;
import PeerServer.protocol.dice.roll_hash;
import PeerServer.protocol.dice.roll_number;
import PeerServer.protocol.dice.RandomNumbers;
import PeerServer.protocol.gameplay.*;
import PlayerInput.DumbBotInterfaceProtocol;
import PlayerInput.MyEntry;
import PlayerInput.PlayerInterface;
import PlayerInput.RemotePlayer;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.esotericsoftware.minlog.Log.debug;


public abstract class AbstractProtocol implements Runnable {

	protected int ack_timeout = 4;
	protected int move_timeout = 4;
	protected ProtocolState protocolState = ProtocolState.JOIN_GAME;
	protected String errorMessage = "default";
	protected ArrayList<String> names = new ArrayList<String>();
	protected ArrayList<String> funNames = new ArrayList<String>();
	protected int ack_id = 0;
	protected int their_ack_id = 0;
	protected int myID;
	protected String myName;
	protected Set<Integer> acknowledgements = new HashSet<Integer>();
	protected BlockingQueue<String> commandQueue = new LinkedBlockingQueue<String>();

 // GAME ENGINE RELATED
	protected State state;
	protected GameEngine engine = null;
	protected ArrayList<Player> startingPlayers = new ArrayList<Player>();
	protected int numOfPlayers;
	protected int firstPlayerId = -1;
	protected int currentPlayerId = -1;
	protected NetworkArbitration networkArbitration = new NetworkArbitration();
	protected Thread gameEngineThread;
	protected int defendingTerId;
	protected int attackingPlayerId;

	// maps a player id with a blocking queue to notify them about their responses
	// or take response from them (if they are local players)
	protected HashMap<Integer, BlockingQueue<Entry<Integer, RequestReason>>> queueMapping =
			new HashMap<Integer, BlockingQueue<Entry<Integer, RequestReason>>>();
	
	protected BlockingQueue<Object> localPlayersQueue = new LinkedBlockingQueue<Object>();
	
// DIE ROLLS
//TODO: need to change the amount of faces accordingly!
	protected RandomNumbers randGenerator;
	protected byte[] randomNumber;	
	// if these are set to -1 it means that we are rolling for number of players
	protected int attackerDiceNum = -1;
	protected int defenderDiceNum = -1;

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
	protected abstract String receiveCommand(boolean ignoreQueue);
	protected abstract void sendLeaveGame(int code, String reason);
	protected abstract void handleLeaveGame(String command);
	protected abstract void acknowledge(int ack);
	protected abstract void handleTimeout(String command);

	/**
	 * Runs the protocol
	 */
	public void run(){
		if(state.getTerritoryIds().size() == 0){
			RiskMapGameBuilder.addRiskTerritoriesToState(state);
		}		
		while(protocolState != null){
			if(engine == null)
				takeSetupAction();
			else 
				takeGameAction();
		}
		
		System.out.println("end");
	}


	// used to recognise when we should leave the setup stage
	private int armiesPlaced = 0;
	private int armiesToBePlaced;
	
	/**
	 * Manages the different states and associated commands.
	 * Plays the whole game and handles the interaction between peers
	 * @param
	 * @return
	 */
	protected void takeGameAction() {
		
		// the protocol can be interrupted by the timer
		// and by the remote protocol - if it detects an invalid command
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
		
        switch (protocolState) {
	        case START_GAME:
	            debug("\n START GAME");
	            networkArbitration.setProtocolThread(Thread.currentThread());
	            armiesToBePlaced = ArmyUtils.getStartingArmies(state) * numOfPlayers;
	           
	            /// roll setup dice - to get the first player
	            // and to shuffle cards
	            setupRolls(); 
	            
	            protocolState = ProtocolState.SETUP_GAME;
	            break;
	       case SETUP_GAME:
	    	   debug("\n SETUP_GAME");
	    	   
	    	   // start the game engine
	    	   if(gameEngineThread == null){
		    	   gameEngineThread = new Thread(engine);		    	   
		    	   gameEngineThread.start();
		    	   currentPlayerId = firstPlayerId;
	    	   }
	    	   setupGame();
	    	   
			   break;
            case PLAY_CARDS:
	            debug("\n PLAY_CARDS");
	            
	            // send / receive play cards
	            // it transfers us to the next state
	            playCards();
	            break;	
            case DEPLOY:
				debug("\n DEPLOY");
				
				// send / receive deploy command
				deploy();
				break;
            case ATTACK:
            	debug("\n ATTACK");
            	
            	// send / receive attack command
				attack();
	            break;
            case DEFEND:
				debug("\n DEFEND");
				defend(); 
				break;
            case ROLL:
	            debug("\n ROLL");
	            gameRoll();
	            protocolState = ProtocolState.ATTACK_CAPTURE;
	            break;
            case ATTACK_CAPTURE:
				debug("\n ATTACK_CAPTURE");
				attackCapture();
	            break;
            case FORTIFY:
				debug("\n FORTIFY");
				fortify(""); // we only get here if its our turn
								// otherwise this is called from attack
	            break;
            default:
				break;

		}
	}



// ===============================================================
//  MAIN GAME COMMANDS
// ===============================================================
	
	/**
	 * Used at the stage when the players are placing their armies. 
	 * At the very beginnign of the game.
	 */
	protected void setupGame(){
		nextStateAfterAck = ProtocolState.SETUP_GAME;
	
		if(currentPlayerId == myID){
			// retrieve a move from the local player
			setup setup = (setup) getResponseFromLocalPlayer();
			
			setup.ack_id = ack_id;
			ack_id++;
			armiesPlaced++;
			
			if(armiesPlaced == armiesToBePlaced){
				// transfer to the next state if necessary
				nextStateAfterAck = ProtocolState.PLAY_CARDS;
			}
			
			String setupString = Jsonify.getObjectAsCommand(setup);
			// send to all clients / send to server
			sendCommand(setupString, null, true);
		}
		else {
			String command = "";
			while(command == ""){
				command = receiveCommand(false);
			}
			
			setup setup = null;
			try{
				setup = (setup) Jsonify.getObjectFromCommand(command, setup.class);
			} catch (ClassCastException e){
				sendLeaveGame(200, "Wrong command. Expected setup.");
				return;
			}
			
			armiesPlaced++;
			
			if(armiesPlaced == armiesToBePlaced){
				nextStateAfterAck = ProtocolState.PLAY_CARDS;
			}
			
			notifyPlayerInterface(setup, setup.player_id);
			their_ack_id = setup.ack_id;
			
		 	sendCommand(command, setup.player_id, true);
		 	acknowledge(setup.ack_id);
		}
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		currentPlayerId = (currentPlayerId + 1) % numOfPlayers;
		protocolState = nextStateAfterAck;
	}
	

	protected void playCards(){
		nextStateAfterAck = ProtocolState.DEPLOY;
		System.out.println("PROTOCOL: CARDS\n");

		if(currentPlayerId == myID){
			//create play_cards object 
			play_cards pc = (PeerServer.protocol.cards.play_cards) getResponseFromLocalPlayer();
			String playCardsString = Jsonify.getObjectAsCommand(pc);
			
			sendCommand(playCardsString, null, true);
		}
		//someone sent us a command 
		else {
			String command = "";
			while(command == ""){
				command = receiveCommand(false);
			}
			
			play_cards pc = (PeerServer.protocol.cards.play_cards) Jsonify.getObjectFromCommand(command, play_cards.class);
			
			if(pc == null){
			}
			
			//send command to all players execpt the one who sent it to us
			notifyPlayerInterface(pc, pc.player_id);
			their_ack_id = pc.ack_id;
			
			sendCommand(command, pc.player_id, true);
			acknowledge(their_ack_id);
		}
		protocolState = nextStateAfterAck;
	}

	
	
	protected void deploy(){
		System.out.println("PROTOCOL: DEPLOY\n");
		nextStateAfterAck = ProtocolState.ATTACK;

		//we are sending command 
		if(currentPlayerId == myID){
			//create deploy object based on player choices
			deploy deploy = (deploy) getResponseFromLocalPlayer();
			String deployString = Jsonify.getObjectAsCommand(deploy);
			
			//send to all clients or just host if you are client 
			sendCommand(deployString, null, true);
		}
		else{
			String command = "";
			while(command == ""){
				command = receiveCommand(false);
			}
			
			deploy deploy = (deploy) Jsonify.getObjectFromCommand(command, deploy.class);
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
			String attackString = Jsonify.getObjectAsCommand(attack);
			
			if(attack.payload == null){
				protocolState = ProtocolState.FORTIFY; // we dont attack, send fortify instead
				return;
			}
			else{
				attackingPlayerId = attack.player_id;
				defendingTerId = attack.payload[1];
				nextStateAfterAck = ProtocolState.DEFEND;
			}
			
			if(attack.payload != null)
				attackerDiceNum = attack.payload[2];
			
			sendCommand(attackString, null, true);
		}
		//someone sent us the command  player id if parsing
		else{
			String command = "";
			while(command == ""){
				command = receiveCommand(false);
			}
			if(command.contains("fortify")){
				fortify(command);
				return;
			}
				
			
			attack attack = (attack) Jsonify.getObjectFromCommand(command, attack.class);
			//check its not null
			if(attack == null){
			}
			
			notifyPlayerInterface(attack, attack.player_id);
			
			if(attack.payload != null)
				attackerDiceNum = attack.payload[2];
			
			their_ack_id = attack.ack_id;

		//	if(attack.payload == null)
		//		nextStateAfterAck = ProtocolState.FORTIFY;
		//	else{
			
			attackingPlayerId = attack.player_id;
			defendingTerId = attack.payload[1];
			nextStateAfterAck = ProtocolState.DEFEND;
			
		//	}
			
			sendCommand(command, attack.player_id, true);
			acknowledge(their_ack_id);
			// TODO: dice roll?
		}
		protocolState = nextStateAfterAck;
		if(protocolState == ProtocolState.DEFEND){
			Territory defendingTer = TerritoryUtils.getTerritoryByNumId(state, defendingTerId);
			Player defendingOwner = PlayerUtils.getTerritoryOwner(state, defendingTer);
			currentPlayerId = defendingOwner.getNumberId();
		}
	}

	protected void defend(){
		System.out.println("PROTOCOL: DEFEND\n");
		//we are sending command 
		nextStateAfterAck = ProtocolState.ROLL;
		if(currentPlayerId == myID){
			defend defend = (defend) getResponseFromLocalPlayer();
			String defendString = Jsonify.getObjectAsCommand(defend);
			
			defenderDiceNum = defend.payload;
			sendCommand(defendString, null, true);
		}
		//someone sent us the command  player id if parsing
		else{
			//parse command into deploy object
			String command = "";
			while(command == ""){
				command = receiveCommand(false);
			}
			
			defend defend = (defend) Jsonify.getObjectFromCommand(command, defend.class);
			if(defend == null){
			}

			notifyPlayerInterface(defend, defend.player_id);
			their_ack_id = defend.ack_id;
			defenderDiceNum = defend.payload;
			
			sendCommand(command, defend.player_id, true);
			acknowledge(their_ack_id);	
		}
		protocolState = nextStateAfterAck;
		currentPlayerId = attackingPlayerId;
	}

	
	protected void attackCapture(){
		System.out.println("PROTOCOL: ATTACK_CAPTURE\n");

		nextStateAfterAck = ProtocolState.ATTACK;
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: check whether we actually captured a territory?
		if(engine.isCountryTaken()){
			engine.resetCountryTaken();
			System.out.println("PROTOCOL: NOTICED CAPTURED ================");
			
			if(currentPlayerId == myID){
				attack_capture attack_capture = (attack_capture) getResponseFromLocalPlayer();
				String attack_captureString = Jsonify.getObjectAsCommand(attack_capture);
				
				sendCommand(attack_captureString, null, true);
			}
			//someone sent us the command  player id if parsing
			else{
				String command = "";
				while(command == ""){
					command = receiveCommand(false);
				}
				
				//parse command into deploy object
				attack_capture attack_capture = (attack_capture) Jsonify.getObjectFromCommand(command, 
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


	protected void fortify(String command){
		System.out.println("PROTOCOL: FORTIFY\n");
		System.out.println(currentPlayerId);
		nextStateAfterAck = ProtocolState.PLAY_CARDS; // TODO: change this
		if(currentPlayerId == myID){
			fortify fortify = (fortify) getResponseFromLocalPlayer();
			String fortifyString = Jsonify.getObjectAsCommand(fortify);

			sendCommand(fortifyString, null, true);
		}
		//someone sent us the command  player id if parsing
		else{
			// we dont need to receive a command since we already got it
			fortify fortify = (fortify) Jsonify.getObjectFromCommand(command, 
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

	protected void gameRoll(){
		System.out.println("PROTOCOL: in dice rolling");
		SeededGenerator diceRoller = new SeededGenerator();
		

			getHashes(diceRoller);
			getNumbers(diceRoller);
			
			try {
				diceRoller.finalise();
			} catch (HashMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int result;
			
			System.out.println("def and attck: " + defenderDiceNum + "  " + attackerDiceNum);
			for(int i = 0; i < (defenderDiceNum + attackerDiceNum); i++){
				result = (int)(diceRoller.nextInt() % 6) + 1;
				networkArbitration.addDieThrowResult(result);
				System.out.print(result + "  ");
			}
			
	}
	
	
	private void setupRolls() {
		SeededGenerator playersRoller = new SeededGenerator();
		SeededGenerator cardShuffler = new SeededGenerator();
		
		// get first player
		getHashes(playersRoller);
		getNumbers(playersRoller);
		
		try {
			playersRoller.finalise();
		} catch (HashMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		firstPlayerId = (int) (playersRoller.nextInt() % numOfPlayers);
		networkArbitration.setFirstPlayerId(firstPlayerId);
		
		System.out.println("GOT FIRST PLAYER: " + firstPlayerId + " ===========================");
		
		getHashes(cardShuffler);
		getNumbers(cardShuffler);
		
		try {
			cardShuffler.finalise();
		} catch (HashMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Card> allCards = CardUtils.getUnownedCards(state);
		int cardNum = allCards.size();
		System.out.println("Num of cards: " + cardNum);
		int randomIndex;
		for(int i = 0; i < cardNum; i++){
			int next = (int)(cardShuffler.nextInt() % cardNum);
			Collections.swap(allCards, i, next);
		}
		
		CardUtils.setShuffledToTrue();
	}
	
	
	
	
	private BlockingQueue<String> rollNumberQueue = new LinkedBlockingQueue<String>();
	private void getHashes(SeededGenerator generator) {
		int hashCount = 1;
		
	// generate and send our own hash
		randomNumber = generator.generateNumber();
		byte[] hash;
		try {
			hash = generator.hashByteArr(randomNumber);
			String hashStr = generator.byteToHex(hash);

			roll_hash rh = new roll_hash(hashStr, myID);
			generator.addHash(myID, hashStr);
			sendCommand(Jsonify.getObjectAsCommand(rh), null, false);
			
		} catch (HashMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	// receive others hashes
	long startTime = System.currentTimeMillis();
		long currentTime;
		String command;
		while(true){
			System.out.println(hashCount);
			if(hashCount == numOfPlayers)
				break;
			
			System.out.println(hashCount + "  " + numOfPlayers  );

			
			command = receiveCommand(false);
			if(!command.contains("roll_hash")){
				if(command.contains("roll_number"))
					try {
						rollNumberQueue.put(command);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else{
					try {
						commandQueue.put(command);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				continue;
			}
			
			roll_hash rollHash = (roll_hash) Jsonify.getObjectFromCommand(command, roll_hash.class);
			hashCount++;
			try {
				generator.addHash(rollHash.player_id, rollHash.payload);
			} catch (HashMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sendCommand(command, rollHash.player_id, false);
	
			
		//if there is timeout
			currentTime = System.currentTimeMillis();
			if(startTime - currentTime > ack_timeout * 1000)
				break;
		}
	}
	
	

	private void getNumbers(SeededGenerator generator) {
		// sendMyNumber
		int numberCount = 1;
		
		String ranNumStr = generator.byteToHex(randomNumber);
		roll_number rn = new roll_number(ranNumStr, myID);

		try {
			generator.addNumber(myID, ranNumStr);
		} catch (HashMismatchException e) {
			e.printStackTrace();
		}

		String rnStr = Jsonify.getObjectAsCommand(rn);
		sendCommand(rnStr, null, false);
		
		long startTime = System.currentTimeMillis();
		long currentTime;
		String command = "";
		while(true){
			
			if(numberCount == numOfPlayers)
				break;
			
			if(rollNumberQueue.isEmpty()){
				command = receiveCommand(true);
				if(!command.contains("roll_number")){
					if(command != "")
						commandQueue.add(command);
					continue;
				}
			}
			else{
				try {
					command = rollNumberQueue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			roll_number rollNum = (roll_number) Jsonify.getObjectFromCommand(command, roll_number.class);
			numberCount++;
			System.out.println(numberCount + "  " + numOfPlayers  );
			
			try {
				generator.addNumber(rollNum.player_id, rollNum.payload);
			} catch (HashMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sendCommand(command, rollNum.player_id, false);
			
			// if there is timeout
			currentTime = System.currentTimeMillis();
			if(startTime - currentTime > ack_timeout * 1000)
				break;
		}
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
			playerInterface = new DumbBotInterfaceProtocol(newSharedQueue, id);
			localPlayersQueue = newSharedQueue;
		}
		else{	
			BlockingQueue<Entry<Integer, RequestReason>> newSharedQueue = new LinkedBlockingQueue<Entry<Integer, RequestReason>>();
			playerInterface = new RemotePlayer(newSharedQueue, Thread.currentThread(), this, state);
			queueMapping.put(id, newSharedQueue); // the mappigng stores all interfaces
		}

		Player newOne;
		if(name != "")
			newOne = new Player(playerInterface, id, name);
		else
			newOne = new Player(playerInterface, id);

		if(localPlayer){
			((DumbBotInterfaceProtocol) playerInterface).setPlayer(newOne);
		}
		
		
		startingPlayers.add(newOne);
		numOfPlayers++;
		System.out.println("adding new player " + numOfPlayers);
		
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
				queue.put(new MyEntry(null, RequestReason.ATTACK_CHOICE_FROM));
				
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
		else if(response instanceof play_cards){
			play_cards play_cards = (play_cards) response;
			try {
				if(play_cards.payload == null){
					queue.put(new MyEntry(null, null));
					queue.put(new MyEntry(null, null));
					queue.put(new MyEntry(null, null));
				}
				else{
					int[][] payload = play_cards.payload.cards;
					for(int i = 0; i < payload.length; i++){
						try {
							queue.put(new MyEntry(payload[i][0], null));
							queue.put(new MyEntry(payload[i][1], null));
							queue.put(new MyEntry(payload[i][2], null));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
				}
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
					wait(5000);
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
