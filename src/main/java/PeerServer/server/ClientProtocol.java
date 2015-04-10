package PeerServer.server;

import static com.esotericsoftware.minlog.Log.debug;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTable.PrintMode;

import com.esotericsoftware.minlog.Log;

import GameBuilders.RiskMapGameBuilder;
import GameEngine.GameEngine;
import GameState.Player;
import GameState.State;
import GeneralUtils.Jsonify;
import PeerServer.protocol.general.acknowledgement;
import PeerServer.protocol.general.leave_game;
import PeerServer.protocol.general.timeout;
import PeerServer.protocol.setup.accept_join_game;
import PeerServer.protocol.setup.initialise_game;
import PeerServer.protocol.setup.join_game;
import PeerServer.protocol.setup.ping;
import PeerServer.protocol.setup.players_joined;
import PeerServer.protocol.setup.ready;
import PeerServer.protocol.setup.reject_join_game;
import PeerServer.server.HostProtocol.ChangeState;
import PlayerInput.DumbBotInterface;
import PlayerInput.PlayerInterface;
import PlayerInput.RemotePlayer;


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
				protocolState = join_game("");
				break;		
			case JOIN_RESPONSE:
				debug("\nJOIN_RESPONSE");
				command = client.receive();
				if(command.contains("accept"))
					protocolState = accept_join_game(command);
				else
					protocolState = reject_join_game(command);
				break;	
			case PLAYERS_JOINED:
				debug("\nPLAYERS_JOINED");
				command = client.receive();
				// while we still wait for players joined the server
				// might send us ping command
				if(command.contains("ping"))
					protocolState = ping(command);
				else
					protocolState = players_joined(command);
				break;	
			case PING:
				debug("\n PING");
				command = client.receive();
				// server might also say that its ready or that there
				// are not enough players and we have to disconnect
				if(command.contains("ready"))
					protocolState = ready(command);
				else if(command.contains("leave"))
					protocolState = leave_game(command);
				else
					protocolState = ping(command);
				break;
			case ACK:
				debug("\n ACKNOWLEDGE");
				protocolState = acknowledge("");
				break;
			case INIT_GAME:
				debug("\n INIT_GAME");
				command = client.receive();
				if(command.contains("timeout"))
					protocolState = timeout(command);
				if(command.contains("leave"))
					protocolState = leave_game(command);
				else
					protocolState = init_game(command);
				break;
			case SETUP_GAME:
				debug("\n SETUP_GAME");
				command = client.receive();
				protocolState = setup_game(command);
				break;
			case LEAVE_GAME:
				debug("\n LEAVE_GAME");
				protocolState = leave_game("");
				break;
			default:
				System.out.println("IN DEFAULT not good");
				break;
		}
	}

	
	//*********************** GAME SETUP ******************************

	@Override
	protected ProtocolState join_game(String command) {
		// send request to join the game
		String name = getRandomName();
		join_game join = new join_game(new Integer[]{1}, new String[]{}, name, "key");
		client.send(Jsonify.getObjectAsJsonString(join));
		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(join));

		return ProtocolState.JOIN_RESPONSE;
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
		// got it from host to be informed about players
		players_joined players = (players_joined) Jsonify.getJsonStringAsObject(command, players_joined.class);	
		if(players == null){
			leaveCode = 200;
			leaveReason = "Expected players_joined command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(players));
		
		// creating all players specified by the protocol
		Object[][] playersDetails = players.payload;
		PlayerInterface playersInt;
			
		Player player;
		for(Object[] details : playersDetails){
			
			int id = ((Double) details[0]).intValue();
			
			if(((Double)details[0]).intValue() == myID)
				playersInt = localPlayer;
			else{
				BlockingQueue<Object> newSharedQueue = new LinkedBlockingQueue<Object>();
				playersInt = new RemotePlayer(newSharedQueue);
				queueMapping.put(id, newSharedQueue);
			}
			
			String name = (String) details[1];
		
			// preventing duplicates
			int i = 1;
			while(names.contains(name)){
				name = details[1] + " " + i;
				i++;
			}
			
			player = new Player(playersInt, id, name);
			names.add(name);
			
		//	interfaceMapping.put(player.getNumberId(), playersInt);
			startingPlayers.add(player);	
			numOfPlayers++;
			
			player.setPublicKey((String)details[2]);
		}
				
		return ProtocolState.PLAYERS_JOINED;
	}


	@Override
	protected ProtocolState ping(String command){
		ping ping = (ping) Jsonify.getJsonStringAsObject(command, ping.class);
		if(ping == null){
			leaveCode = 200;
			leaveReason = "Expected ping command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(ping));
		
		// acknowledge the ping
		acknowledgements.add(ping.player_id);
		
		// sent from host
		if(ping.payload != null){
			state.setPlayers(startingPlayers);
	
			//	TODO: ask in UI: do you still want to play?
			ping response = new ping(ping.payload, myID);
			
			// send your ping 
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
		// -2 because we dont consider ourselves here
		if (acknowledgements.size() != startingPlayers.size() - 1){
			for(Player p : startingPlayers){
				if(!acknowledgements.contains(p.getNumberId())){
					if(p.getNumberId() != myID)
						removePlayer(p.getNumberId());
				}
			}
		}
	
		// TODO: think about it 
		

		if(ack_id == 0)
			ack_id = ready.ack_id;
		else{
			if(ack_id + 1 != ready.ack_id){
				System.out.println("WRONG ACK_ID, missed sth from server");
			}
			ack_id = ready.ack_id;
		}
		
		return ProtocolState.ACK;
	}

	@Override
	protected ProtocolState acknowledge(String command) {
		acknowledgement ack = new acknowledgement(ack_id, myID);
		System.out.println("\nSEND: " + Jsonify.getObjectAsJsonString(ack));
		client.send(Jsonify.getObjectAsJsonString(ack));
		
		return ProtocolState.INIT_GAME;
	}
	
	@Override
	protected ProtocolState init_game(String command){		
		initialise_game init = (initialise_game) Jsonify.getJsonStringAsObject(command, initialise_game.class);
		if(init == null){
			leaveCode = 200;
			leaveReason = "Expected init game command";
			return ProtocolState.LEAVE_GAME;
		}
		
		System.out.println("\nGOT: " + Jsonify.getObjectAsJsonString(init));
		versionPlayed = init.payload.version;
		featuresUsed = init.payload.supported_features;
		
		State state = new State(startingPlayers);
		engine = new GameEngine(state);
		
		diceRoller.setFaceValue(startingPlayers.size());
		
		return ProtocolState.LEAVE_GAME;
	}
	

	@Override
	protected ProtocolState timeout(String command){
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
			
			return null;
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
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.gameplay.setup.class);
		return protocolState;	
	}

	
	public static void main(String[] args) {
		ClientProtocol protocol = new ClientProtocol();
		protocol.run();
	}

	public void run() {
		// choosing who playes on local side
		localPlayer = new DumbBotInterface();
		
		// localhost should be replaced with an argument args[0], port args[1]
		client = new Client("localhost", 4444);
		
		super.run();
		
		client.close();
	}


	@Override
	protected void sendCommand(String command, Integer exceptId) {
		if(exceptId != null)
			return;
		
		client.send(command);
	}



	
}
