package PeerServer.server;

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
	String leaveReason;
	String leaveCode;
	PlayerInterface localPlayer;
	
	
	//*********************** GAME SETUP ******************************

	@Override
	protected ProtocolState join_game(String command) {
		System.out.println("JOIN");
		
		// empty string meand there was no command and we are joining the game
		if(command == ""){
			// send request to join the game
			String name = getRandomName();
			join_game join = new join_game(new Integer[]{1}, new String[]{}, name, "key");
			client.send(Jsonify.getObjectAsJsonString(join));
			
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
				leaveCode = "200";
				leaveReason = "Expected accept_join_game or reject_join_game command";
				return ProtocolState.LEAVE_GAME;
			}
		}
	}

	
	@Override
	protected ProtocolState accept_join_game(String command) {
		System.out.println("ACCEPT");
		
		accept_join_game join = (accept_join_game) Jsonify.getJsonStringAsObject(command, accept_join_game.class);
		if(join == null){
			leaveCode = "200";
			leaveReason = "Expected accept_join_game command";
			return ProtocolState.LEAVE_GAME;
		}
		
		// retrieve all details from host response
		myID = join.player_id;
		ack_timeout = join.acknowledgement_timeout;
		move_timeout = join.move_timeout;
		
		return ProtocolState.PLAYERS_JOINED;
	}


	@Override
	protected ProtocolState reject_join_game(String reason) {
		System.out.println("REJECT");
		
		reject_join_game reject = (reject_join_game) Jsonify.getJsonStringAsObject(reason, reject_join_game.class);
		if(reject == null){
			leaveCode = "200";
			leaveReason = "Expected reject_join_game command";
			return ProtocolState.LEAVE_GAME;
		}
		// TODO: present in UI preferably
		System.out.println(reject.error_message);
		
		// just leave the game
		leaveCode = "400";
		leaveReason = "Rejected";
		return ProtocolState.LEAVE_GAME;
	}


	@Override
	protected ProtocolState players_joined(String command){	
		System.out.println("PLAYERS_JOINED");
		
		// got it from host to be informed about players
		players_joined players = (players_joined) Jsonify.getJsonStringAsObject(command, players_joined.class);	
		if(players != null){
			// creating all players specified by the protocol
			String[][] playersDetails = players.players;
			PlayerInterface playersInt;
				
			Player player;
			
			for(String[] details : playersDetails){
				if(Integer.parseInt(details[0]) == myID)
					playersInt = localPlayer;
				else
					playersInt = new RemotePlayer();
				
				String name;
				// preventing duplicates
				if(names.contains(details[1]))
					name = details[1] + " 1";
				else
					name = details[1];
			
				player = new Player(playersInt, 0, Integer.parseInt(details[0]), name);
				names.add(name);
				
				// add the player to the mapping of players
				// and their interfaces for future use
				
				// TODO: null as local?
				interfaceMapping.put(player.getNumberId(), playersInt);
				startingPlayers.add(player);		
			}
				
				return ProtocolState.PLAYERS_JOINED;
		}
		// if it wasnt a players_joined command it must be the ping command
		else
			return ping(command);
		
	}


	@Override
	protected ProtocolState ping(String command){
		System.out.println("PING");
		
		if(command.contains("ready"))
			return ready(command);
		
		if(command.contains("leave"))
			return leave_game(command);
		
		ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);
		if(ping == null){
			leaveCode = "200";
			leaveReason = "Expected ping command";
			return ProtocolState.LEAVE_GAME;
		}
		
		// sent by client
		if(ping.payload == null){
			acknowledgements.add(ping.player_id);
		}
		else{
			if(state.getPlayers().size() == 0){
				// the players in the state might be changed
				// later if some player resigns from playing
				state.setPlayers(startingPlayers);
			}
			
			//	TODO: ask in UI: do you still want to play?
			ping response = new ping(ping.payload, myID);
			client.send(Jsonify.getObjectAsJsonString(response));
		}
		return ProtocolState.PING;
	}
	
	
	@Override
	protected ProtocolState timeout(String command){
		System.out.println("TIMEOUT");
		// game should be ended here..
	
		timeout timeout = (timeout) Jsonify.getJsonStringAsObject(command, timeout.class);
		if(timeout == null){
			leaveCode = "200";
			leaveReason = "Expected timeout command";
			return ProtocolState.LEAVE_GAME;
		}
		
		int playerOut = timeout.payload;
		remove_player(playerOut);
		
		// send the acknowledgement
		acknowledgement ack = new acknowledgement(timeout.ack_id, myID);
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
				leaveCode = "400";
				leaveReason = "Host left...";
				return ProtocolState.LEAVE_GAME;
			}
			
			String[][] payload = leave.payload;
			int responseCode = Integer.parseInt(payload[0][1]);
			String message = payload[1][1];
			
			// TODO: write sth here? Why server finished the game
			// we leave so it doesnt matter
			return null;
		}
		// if command is empty it is us that want to leave
		else{
			String[][] payload = new String[3][2];
			payload[0] = new String[]{"response", leaveCode};
			payload[1] = new String[]{"message", leaveReason};
			// we dont want to receive updates
			payload[2] = new String[]{"receive_updates", "false"};
			leave_game leave = new leave_game(payload, myID);
			
			client.send(Jsonify.getObjectAsJsonString(leave));
			
			// we leave so it doesnt matter
			return null;
		}
	}
	
	@Override
	protected ProtocolState ready(String command){
		System.out.println("READY");
		ready ready = (ready) Jsonify.getJsonStringAsObject(command, ready.class);
		if(ready == null){
			leaveCode = "200";
			leaveReason = "Expected ready command";
			return ProtocolState.LEAVE_GAME;
		}
		
		if (acknowledgements.size() != startingPlayers.size()){
			for(Player p : startingPlayers){
				if(!acknowledgements.contains(p.getNumberId()))
					startingPlayers.remove(p);
			}
		}
		
		acknowledgement ack = new acknowledgement(ready.ack_id, myID);
		client.send(Jsonify.getObjectAsJsonString(ack));
		
		return ProtocolState.INIT_GAME;
	}

	
	@Override
	protected ProtocolState init_game(String command){
		System.out.println("INIT_GAME");
		if(command.contains("timeout"))
			return timeout(command);
		
		String received = client.receive();
		initalise_game init = (initalise_game) Jsonify.getJsonStringAsObject(received, initalise_game.class);
	
		if(init == null){
			leaveCode = "200";
			leaveReason = "Expected init game command";
			return ProtocolState.LEAVE_GAME;
		}
		
		versionPlayed = init.version;
		featuresUsed = init.supported_features;
		
		return ProtocolState.SETUP_GAME;
	}

	


	@Override
	protected ProtocolState setup_game(String command){
		if(command.contains("timeout"))
			return timeout(command);
		
		
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.setup.class);
		return protocolState;	
	}


	@Override
	protected ProtocolState receive_ack(String command) {
		return null;
	}

	@Override
	protected ProtocolState ping_ack(String command) {
		return null;
	}


	
	public static void main(String[] args) {
		ClientProtocol protocol = new ClientProtocol();
		protocol.state = new State();
		RiskMapGameBuilder.addRiskTerritoriesToState(protocol.state);
		
		// choosing who playes on local side
		protocol.localPlayer = new DumbBotInterface();
		
		// to send receive messages etc
		// localhost should be replaced with an argument args[0], port args[1]
		protocol.client = new Client("localhost", 4444);
		
		// adding all fun names to be randomly picked as players names
		String[] names = new String[]{"Chappie", "Rex", "Monkey", "XXX",
				"Gandalf", "Pinguin", "Chocolate", "Billy", "Panda", "Zebra",
				"Billy the Pinguin", "Mike the Pistacio", "The Machine", "Unknown",
				"RISK Master"};
		protocol.funNames.addAll(Arrays.asList(names));
		
		// sending request to join the game
		protocol.join_game("");
		
		// keeps receiving from server and acting upon what it received
		String command;
		while(true){
			if(protocol.protocolState == ProtocolState.LEAVE_GAME){
				protocol.handleCommand("");
				break;
			}
			else{
				command = protocol.client.receive();
				protocol.handleCommand(command);
			}
		}
		
		protocol.client.close();
	}

	
}
