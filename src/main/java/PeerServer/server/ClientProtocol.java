package PeerServer.server;

import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import PeerServer.protocol.gameplay.setup;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.leave_game;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.*;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import PlayerInput.RemotePlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.esotericsoftware.minlog.Log.debug;


public class ClientProtocol extends AbstractProtocol{

	private Client client;
	private int versionPlayed;
	private String[] featuresUsed;

	// ids of users who sent their pings
	private Set<Integer> acknowledgements = new HashSet<Integer>();

	@Override
	protected void takeSetupAction() {
		//Log.DEBUG = true;
		String command;
		switch(protocolState){
		case JOIN_GAME:
			debug("\nJOIN_GAME");
			sendJoinGame(); // this changes our state accordingly
			break;			
		case PLAYERS_JOINED:
			debug("\nPLAYERS_JOINED");
			command = receiveCommand();
			
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
			command = receiveCommand();
			
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
			command = receiveCommand();
			
			if(command.contains("timeout"))
				handleTimeout(command);
			if(command.contains("leave"))
				handleLeaveGame(command);
			else
				handleInitializeGame(command);
				protocolState = null;
			break;
		default:
			System.out.println("IN DEFAULT not good");
			break;
		}
	}


// =====================================================================
// JOINING GAME
// =====================================================================
	
	protected void sendJoinGame() {
		// send request to join the game
		String name = getRandomName();
		join_game join = new join_game(new Integer[]{1}, new String[]{}, name, "key");
		
		sendCommand(Jsonify.getObjectAsJsonString(join), null, false);
		
		String response = receiveCommand();
		if(!response.contains("accept")){
			handleRejectJoin(response);
		}
		// else we were accepted
		handleAcceptJoin(response);
		protocolState = ProtocolState.PLAYERS_JOINED;
	}


	/**
	 * Retrieve an ID and some information about timeouts, prepare to play the game
	 * @param command
	 */
	protected void handleAcceptJoin(String command) {
		accept_join_game join = (accept_join_game) Jsonify.getJsonStringAsObject(command, accept_join_game.class);
		if(join == null){
			sendLeaveGame(200, "Expected accept_join_game command");
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
		reject_join_game reject = (reject_join_game) Jsonify.getJsonStringAsObject(reason, reject_join_game.class);
		if(reject == null){
			// just leave the game
			sendLeaveGame(400, "Rejected");
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
		players_joined players = (players_joined) Jsonify.getJsonStringAsObject(command, players_joined.class);	
		if(players == null){
			sendLeaveGame(200, "Expected players_joined command");
			return;
		}
		
		// creating all players specified by the protocol
		Object[][] playersDetails = players.payload;

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
			if(((Double)details[0]).intValue() == myID) // TODO: not sure whether we get ourselves form host???
				player = createNewPlayer(name, id, true);
			else // create remote
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
		ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);
		if(ping == null){
			sendLeaveGame(200, "Expected ping command");
			return;
		}
		
		// acknowledge the ping
		acknowledgements.add(ping.player_id);

		// sent from host - respond to them
		if(ping.payload != null){
			state.setPlayers(startingPlayers);

			//	TODO: ask in UI: do you still want to play?
			ping response = new ping(ping.payload, myID);
			sendCommand(Jsonify.getObjectAsJsonString(response), null, false);
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
		ready ready = (ready) Jsonify.getJsonStringAsObject(command, ready.class);
		if(ready == null){
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
		initialise_game init = (initialise_game) Jsonify.getJsonStringAsObject(command, initialise_game.class);
		if(init == null){;
			sendLeaveGame(200, "Expected init game command");
			return;
		}

		versionPlayed = init.payload.version;
		featuresUsed = init.payload.supported_features;
	}


	/**
	 *  Removes the player who has timed out and acknowledges the information
	 * @param command
	 */
	protected void handleTimeout(String command){
		timeout timeout = (timeout) Jsonify.getJsonStringAsObject(command, timeout.class);
		if(timeout == null){
			sendLeaveGame(200, "Expected timeout command");
			return;
		}

		int playerOut = timeout.payload;
		removePlayer(playerOut);
		
		acknowledge(timeout.ack_id);	
	}


	/**
	 * Sends a leaving message to the host with the given leaveCoe and leaveReason
	 * @param leaveCode
	 * @param leaveReason
	 */
	protected void sendLeaveGame(int leaveCode, String leaveReason){
		leave_game leave = new leave_game(leaveCode, leaveReason, false, myID);
		sendCommand(Jsonify.getObjectAsJsonString(leave), null, false);
		
		protocolState = null;
	}
	
	/**
	 * Removes the player who left, unless its a host in which case the game finishes
	 */
	protected void handleLeaveGame(String command){
		leave_game leave = (leave_game) Jsonify.getJsonStringAsObject(command, leave_game.class);
		
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
		sendCommand(Jsonify.getObjectAsJsonString(ack), null, false);
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
	protected String receiveCommand() {
		String receive = client.receive();
		System.out.println("GOT: " + receive + "\n");
		return receive;
	}
	
	
// =====================================================================
// RUNNING THE CLIENT 
// =====================================================================
		
	
	public void run() {
		// localhost should be replaced with an argument args[0], port args[1]
		client = new Client("localhost", 4444);
		state = new State();

		super.run();

		client.close();
	}

	public static void main(String[] args) {
		ClientProtocol protocol = new ClientProtocol();
		protocol.run();
	}




}
