package PeerServer.server;

import static com.esotericsoftware.minlog.Log.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JTable.PrintMode;

import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.leave_game;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.accept_join_game;
import PeerServer.protocol.setup.initalise_game;
import PeerServer.protocol.setup.join_game;
import PeerServer.protocol.setup.ping;
import PeerServer.protocol.setup.players_joined;
import PeerServer.protocol.setup.ready;
import PeerServer.protocol.setup.reject_join_game;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;


public class ClientProtocol extends AbstractProtocol{

	private Client client;
	private int myID;
	private int versionPlayed;
	private String[] featuresUsed;

	// ids of users who sent their pings
	private Set<Integer> acknowledgements = new HashSet<Integer>();
	
	
	// only commands protocol receives over network
	@Override
	protected void handleSetupCommand(String command){
		switch(this.protocolState){
		/*case JOIN_GAME:
				debug("\nJOIN_GAME");
				this.protocolState = join_game(command);			
				break;		
		*/	case PLAYERS_JOINED:
				debug("\n PLAYERS_JOINED");
				this.protocolState = players_joined(command);
				break;
			case PING:
				debug("\n PING");
				this.protocolState = ping(command);
				break;
		/*	case PING_ACK:
				debug("\n PING_ACK");
				this.protocolState = ping_ack(command);
				break;
		*/	case READY:
				debug("\n READY");
				this.protocolState = ready(command);
				break;
		/*	case RECEIVE_ACK:
				debug("\n RECEIVE_ACK");
				this.protocolState = receive_ack(command);
				break;
		*/	case INIT_GAME:
				debug("\n INIT_GAME");
				this.protocolState = init_game(command);
				break;
			case SETUP_GAME:
				debug("\n SETUP_GAME");
				this.protocolState = setup_game(command);
				break;
			default:
				System.out.println("IN DEFAULT not good");
				break;
		}
	}
	
	//*********************** GAME SETUP ******************************

	@Override
	protected ProtocolState join_game(String command) {
		// empty string means there was no command and we are joining the game
		if(command == ""){
			// send request to join the game
			String name = getRandomName();
			join_game join = new join_game(new Integer[]{1}, new String[]{}, name, "key");
			client.send(Jsonify.getObjectAsJsonString(join));
			System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(join));
			// host did not respond correctly
			return ProtocolState.JOIN_GAME;
		}
	
		// otherwise we received a response to our request
		else{
			if(command.contains("accept_join_game")){
				return accept_join_game(command);
			}
			
			if(command.contains("reject_join_game")){
				return reject_join_game(command);
			}
			
			// something went wrong...
			else{
				leaveCode = 200;
				leaveReason = "Expected accept_join_game or reject_join_game command";
				return ProtocolState.LEAVE_GAME;
			}
		}
	}

	
	@Override
	protected ProtocolState accept_join_game(String command) {
		accept_join_game join = (accept_join_game) Jsonify.getJsonStringAsObject(command, accept_join_game.class);
		if(join == null){
			leaveCode = 200;
			leaveReason = "Expected accept_join_game command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(join));
		
		// retrieve all details from host response
		myID = join.payload.player_id;
		ack_timeout = join.payload.acknowledgement_timeout;
		move_timeout = join.payload.move_timeout;
		
		return ProtocolState.PLAYERS_JOINED;
	}


	@Override
	protected ProtocolState reject_join_game(String reason) {
		reject_join_game reject = (reject_join_game) Jsonify.getJsonStringAsObject(reason, reject_join_game.class);
		if(reject == null){
			leaveCode = 200;
			leaveReason = "Expected reject_join_game command";
			return ProtocolState.LEAVE_GAME;
		}
		
		// TODO: present in UI preferably
		System.out.println(reject.payload);
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(reject));
		
		// just leave the game
		leaveCode = 400;
		leaveReason = "Rejected";
		return ProtocolState.LEAVE_GAME;
	}


	@Override
	protected ProtocolState players_joined(String command){	
		if(command.contains("ping"))
			return ping(command);
		
		// got it from host to be informed about players
		players_joined players = (players_joined) Jsonify.getJsonStringAsObject(command, players_joined.class);	
		if(players == null){
			leaveCode = 200;
			leaveReason = "Expected players_joined command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(players));
		
		// creating all players specified by the protocol
		String[][] playersDetails = players.payload;
		PlayerInterface playersInt;
			
		Player player;
		for(String[] details : playersDetails){
			if(Integer.parseInt(details[0]) == myID)
				playersInt = localPlayer;
			else
				playersInt = new RemotePlayer();
			
			String name = details[1];
		
			// preventing duplicates
			int i = 1;
			while(names.contains(name)){
				name = details[1] + " " + i;
				i++;
			}
			
			player = new Player(playersInt, 0, Integer.parseInt(details[0]), name);
			names.add(name);
			interfaceMapping.put(player.getNumberId(), playersInt);
			startingPlayers.add(player);		
		}
				
		return ProtocolState.PLAYERS_JOINED;
	}


	@Override
	protected ProtocolState ping(String command){
		if(command.contains("ready"))
			return ready(command);
		
		if(command.contains("leave"))
			return leave_game(command);
		
		ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);
		if(ping == null){
			leaveCode = 200;
			leaveReason = "Expected ping command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(ping));
		
		// sent by client
		if(ping.payload == null){
			// calculating number of players
			acknowledgements.add(ping.player_id);
		}
		
		// sent from host
		else{
			// it should be 0
			if(state.getPlayers().size() == 0){
				// the players in the state might be changed
				// later if some player resigns from playing
				state.setPlayers(startingPlayers);
			}
			
			//	TODO: ask in UI: do you still want to play?
			ping response = new ping(ping.payload, myID);
			
			System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(response));
			client.send(Jsonify.getObjectAsJsonString(response));
		}
		return ProtocolState.PING;
	}
	
	
	@Override
	protected ProtocolState ready(String command){
		ready ready = (ready) Jsonify.getJsonStringAsObject(command, ready.class);
		if(ready == null){
			leaveCode = 200;
			leaveReason = "Expected ready command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(ready));
		
		// remove all players who have not acknowledged
		if (acknowledgements.size() != startingPlayers.size()){
			for(Player p : startingPlayers){
				if(!acknowledgements.contains(p.getNumberId())){
					removePlayer(p.getNumberId());
				}
			}
		}
		
		acknowledgement ack = new acknowledgement(ready.ack_id, myID);
		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(ack));
		client.send(Jsonify.getObjectAsJsonString(ack));
		
		return ProtocolState.INIT_GAME;
	}

	
	@Override
	protected ProtocolState init_game(String command){
		if(command.contains("timeout"))
			return timeout(command);
		
		if(command.contains("leave"))
			return leave_game(command);
		
		initalise_game init = (initalise_game) Jsonify.getJsonStringAsObject(command, initalise_game.class);
		if(init == null){
			leaveCode = 200;
			leaveReason = "Expected init game command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(init));
		versionPlayed = init.payload.version;
		featuresUsed = init.payload.supported_features;
		
		return ProtocolState.SETUP_GAME;
	}
	

	@Override
	protected ProtocolState timeout(String command){
		System.out.println("TIMEOUT");
	
		timeout timeout = (timeout) Jsonify.getJsonStringAsObject(command, timeout.class);
		if(timeout == null){
			leaveCode = 200;
			leaveReason = "Expected timeout command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(timeout));
		// remove player that has timeout out
		int playerOut = timeout.payload;
		removePlayer(playerOut);
		
		// send the acknowledgement
		acknowledgement ack = new acknowledgement(timeout.ack_id, myID);
		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(ack));
		client.send(Jsonify.getObjectAsJsonString(ack));

		return protocolState;		
	}
	
	
	@Override
	protected ProtocolState leave_game(String command){
		System.out.println("LEAVE");
		
		// if command is not empty the host sent us a leave request
		if(command != ""){
			leave_game leave = (leave_game) Jsonify.getJsonStringAsObject(command, leave_game.class);
			if(leave == null){
				leaveCode = 400;
				leaveReason = "Host left...";
				return ProtocolState.LEAVE_GAME;
			}
			
			System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(leave));
			int id = leave.player_id;
			int responseCode = leave.payload.response;
			String message = leave.payload.message;
			
			// if host is leaving
			if(id == 0){
				return leave_game("");
			}
			
			// removing player thats leaving
			removePlayer(id);
			
			return protocolState;
		}
		// if command is empty it is us that want to leave
		else{
			leave_game leave = new leave_game(leaveCode, leaveReason, false, myID);
			
			System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(leave));
			client.send(Jsonify.getObjectAsJsonString(leave));
			
			return null;
		}
	}
	
	
	@Override
	protected ProtocolState setup_game(String command){
		if(command.contains("timeout"))
			return timeout(command);
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.setup.class);
		return protocolState;	
	}

	
	public static void main(String[] args) {
		ClientProtocol protocol = new ClientProtocol();
		protocol.run();
	}

	private void run() {
		state = new State();
		RiskMapGameBuilder.addRiskTerritoriesToState(state);
		
		// choosing who playes on local side
		localPlayer = new DumbBotInterface();
		
		// localhost should be replaced with an argument args[0], port args[1]
		client = new Client("localhost", 4444);
		
		// sending request to join the game
		join_game("");
		
		// keeps receiving from server and acting upon what it received
		String command;
		while(protocolState != ProtocolState.LEAVE_GAME){
			command = client.receive();
			handleSetupCommand(command);
		}
		
		// send msg about leaving
		leave_game("");
		client.close();
	}
	
}
