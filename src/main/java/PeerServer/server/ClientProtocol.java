package PeerServer.server;

import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.leave_game;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.*;

import static com.esotericsoftware.minlog.Log.debug;


public class ClientProtocol extends AbstractProtocol{

	// TCP client used for sending and receiving commands
	private Client client;
	
	// fields used if some extensions are used
	private int versionPlayed;
	private String[] featuresUsed;


	@Override
	/**
	 * The method called multiple times before the game
	 * takes place - it is used to perform the handshake
	 */
	protected void takeSetupAction() {
		String command;
		
		switch(protocolState){
		case JOIN_GAME:
			debug("\nJOIN_GAME");
			sendJoinGame(); // this changes our state accordingly
			break;			
		case PLAYERS_JOINED:
			debug("\nPLAYERS_JOINED");
			command = receiveCommand(false); // receive command from the host
			
			// while we still wait for players joined the server
			// might send us ping command
			if(command.contains("ping")){
				handlePing(command);
				protocolState = ProtocolState.PING;
			}
			else
				handlePlayersJoined(command);
			break;	
		case PING:
			debug("\n PING");
			command = receiveCommand(false);
			
			// server might also say that its ready or that there
			// are not enough players and we have to disconnect
			if(command.contains("ready")){
				handleReady(command); // changes our state to init_game
			}
			else if(command.contains("leave")){
				handleLeaveGame(command); 
			}
			else
				handlePing(command);
			break;
		case INIT_GAME:
			debug("\n INIT_GAME");
			command = receiveCommand(false); 
			
			if(command.contains("timeout"))
				handleTimeout(command);
			if(command.contains("leave"))
				handleLeaveGame(command);
			else
				handleInitializeGame(command);
				protocolState = ProtocolState.START_GAME; // finish setup stage, start the game
			break;
		default:
			break;
		}
	}


// =====================================================================
// JOINING GAME
// =====================================================================
	
	/**
	 * Sends join game to the host handles the accept / reject response.
	 * Transfers the protocol to the PLAYERS_JOINED state.
	 */
	protected void sendJoinGame() {
		// send request to join the game
		myName = getRandomName(); 
		join_game join = new join_game(new Integer[]{1}, new String[]{}, myName, "key");
		
		sendCommand(Jsonify.getObjectAsCommand(join), null, false);
		
		String response = receiveCommand(false);
		if(!response.contains("accept")){
			handleRejectJoin(response);
		}
		// else we were accepted
		handleAcceptJoin(response);
		protocolState = ProtocolState.PLAYERS_JOINED;
	}


	/**
	 * Retrieve an ID and some information about timeouts, prepare to play the game.
	 * @param command
	 */
	protected void handleAcceptJoin(String command) {
		accept_join_game join = null;
		try{
			join = (accept_join_game) Jsonify.getObjectFromCommand(command, accept_join_game.class);
		} catch (ClassCastException e){
			sendLeaveGame(200, "Expected accept_join_game command");
			return;
		}

		// retrieve all details from host response
		myID = join.payload.player_id;
		ack_timeout = join.payload.acknowledgement_timeout;
		move_timeout = join.payload.move_timeout;
	}


	/**
	 * Retrieve reason for being rejected. Finish the game before it even started.
	 * @param reason
	 */
	protected void handleRejectJoin(String reason) {
		reject_join_game reject = null;
		try{
			reject = (reject_join_game) Jsonify.getObjectFromCommand(reason, reject_join_game.class);
		} catch (ClassCastException e){
			sendLeaveGame(400, "Rejected");
			return;
		}
		
		// TODO: present in UI preferably
		System.out.println(reject.payload);
		protocolState = null;
	}


	/**
	 * Create all necessary players, we were informed about.
	 */
	protected void handlePlayersJoined(String command){	
		// got it from host to be informed about players
		players_joined players = null;
		try{
			players = (players_joined) Jsonify.getObjectFromCommand(command, players_joined.class);	
		} catch (ClassCastException e){			
			sendLeaveGame(200, "Expected players_joined command");
			return;
		}
		
		// creating all players specified by the protocol
		Object[][] playersDetails = players.payload;

		System.out.println(playersDetails.length);
		
		Player player;
		for(Object[] details : playersDetails){

			int id = ((Double) details[0]).intValue();
			String name = (String) details[1];

			// preventing duplicates
			int i = 1;
			while(names.contains(name)){
				name = details[1] + " " + i;
				i++;
			}
			
			// create local player
			if(((Double)details[0]).intValue() == myID)
				player = createNewPlayer(name, id, true);
			
			else // create remote player
				player = createNewPlayer(name, id, false);
		
			names.add(name);
			player.setPublicKey((String)details[2]);
		}
	}

	
// =====================================================================
// HANDLING VARIOUS COMMANDS
// =====================================================================
	
	/**
	 * Adds the sending player to its array of players who sent and acknowledgement
	 * If the sender is a host, responds to the host with its own ping.
	 * @param command
	 */
	protected void handlePing(String command){
		ping ping = null;
		try{	
			ping = (ping) Jsonify.getObjectFromCommand(command, ping.class);
		} catch (ClassCastException e){
			sendLeaveGame(200, "Expected ping command");
			return;
		}
		
		// acknowledge the ping
		acknowledgements.add(ping.player_id);

		// sent from host - respond to them
		if(ping.payload != null){
			state.setPlayers(startingPlayers);

			//	TODO: ask in UI: do you still want to play?
			ping response = new ping(null , myID);
			sendCommand(Jsonify.getObjectAsCommand(response), null, false);
		}
	}

	
	/**
	 * Prepares to play the game, retrieves information about the number of players
	 * and checks whether it got it correct from ping.
	 * 
	 * @param command
	 * @return
	 */
	protected void  handleReady(String command){
		ready ready = null;
		try{
			ready = (ready) Jsonify.getObjectFromCommand(command, ready.class);
		} catch (ClassCastException e){
			sendLeaveGame(200, "Expected ready command");
			return;
		}

		// remove all players who have not acknowledged
		// -1 because we don't consider ourselves here
		if (acknowledgements.size() != startingPlayers.size() - 1){
			for(Player p : startingPlayers){
				if(!acknowledgements.contains(p.getNumberId())){
					if(p.getNumberId() != myID)
						removePlayer(p.getNumberId());
				}
			}
		}

		ack_id = ready.ack_id;
		acknowledge(ack_id); // acknowledge this message from the server
		protocolState = ProtocolState.INIT_GAME;
	}

	
	
	/**
	 *  Retrieves the information about a version played and features to be used
	 * @param command
	 */
	protected void handleInitializeGame(String command){		
		initialise_game init = null;
		try{
			init = (initialise_game) Jsonify.getObjectFromCommand(command, initialise_game.class);
		} catch (ClassCastException e){
			sendLeaveGame(200, "Expected init game command");
			return;
		}


		engine = new GameEngine(state, networkArbitration);

		versionPlayed = init.payload.version;
		featuresUsed = init.payload.supported_features;
	}


	/**
	 *  Removes the player who has timed out and acknowledges the information
	 * @param command
	 */
	protected void handleTimeout(String command){
		timeout timeout  = null;
		try{
			timeout = (timeout) Jsonify.getObjectFromCommand(command, timeout.class);
		} catch (ClassCastException e){
			sendLeaveGame(200, "Expected timeout command");
			return;
		}

		int playerOut = timeout.payload;
		removePlayer(playerOut);
		acknowledge(timeout.ack_id);	
	}


	/**
	 * Sends a leaving message to the host with the given leaveCode and leaveReason
	 * @param leaveCode
	 * @param leaveReason
	 */
	protected void sendLeaveGame(int leaveCode, String leaveReason){
		leave_game leave = new leave_game(leaveCode, leaveReason, false, myID);
		sendCommand(Jsonify.getObjectAsCommand(leave), null, false);
		
		protocolState = null;
	}
	
	/**
	 * Removes the player who left, unless its a host in which case the game finishes
	 */
	protected void handleLeaveGame(String command){
		leave_game leave = (leave_game) Jsonify.getObjectFromCommand(command, leave_game.class);
		
		int id = leave.player_id;
		int responseCode = leave.payload.response;
		String message = leave.payload.message;

		// if host is leaving
		if(id == 0){
			protocolState = null;
		}

		// removing player thats leaving
		removePlayer(id);
	}

	
	
// =====================================================================
// SENDING AND RECEIVING COMMANDS AND ACKNOWLEDGEMENTS
// =====================================================================
	
	/**
	 * Sends an acknowledgement with the given ack_id
	 * @param acknowledgementId
	 */
	protected void acknowledge(int acknowledgementId) {
		acknowledgement ack = new acknowledgement(acknowledgementId, myID);
		sendCommand(Jsonify.getObjectAsCommand(ack), null, false);
	}
	
	/**
	 * Sends a command to the host, if exceptIs is no null ignores the call
	 * and does nothing
	 */
	protected void sendCommand(String command, Integer exceptId, boolean ack) {
		if(exceptId != null)
			return;

		System.out.println("SEND: " + command + "\n");
		client.send(command);
	}


	/**
	 * Receives a command from the host.
	 */
	protected String receiveCommand(boolean ignoreQueue) {
		String received = "";
		
		if(!ignoreQueue && commandQueue.size() != 0){
			System.out.println("queue not empty!");
			try {
				received = commandQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			received = client.receive();
		}
		
		System.out.println("GOT: " + received + "\n");
		return received;
	}
	
	
// =====================================================================
// RUNNING THE CLIENT 
// =====================================================================
		
	public ClientProtocol(State state) {
        this.state = state;
    }
	public ClientProtocol() {
        this.state = new State();
    }

	public void run() {
		// localhost should be replaced with an argument args[0], port args[1]
		client = new Client("138.251.212.103", 4444);

        super.run();

        gameEngineThread.stop();
		client.close();
	}


	public static void main(String[] args) {

		ClientProtocol protocol = new ClientProtocol();
		protocol.run();
	}


}
