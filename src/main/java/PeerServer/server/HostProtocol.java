package PeerServer.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import PeerServer.protocol.setup.*;
import PeerServer.protocol.general.*;
import PeerServer.protocol.dice.*;
import PeerServer.protocol.cards.*;
import PeerServer.protocol.gameplay.*;
import GameBuilders.RiskMapGameBuilder;
import GameState.Player;
import GameState.State;
import GameUtils.PlayerUtils;
import GeneralUtils.Jsonify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


/**
 * An instance of this class represents a game that
 * is currently being played over the network.
 * 
 */
public class HostProtocol extends AbstractProtocol {
	
	private int maxNoOfPlayers = 6;		//min is 3	
	private int gameLaunchTime = 60; // TODO: what is this?

	// mappings of features / versions to the count of how many clients support these
	private HashMap<String, Integer> supportedFeatures = new HashMap<String, Integer>();
	private HashMap<Integer, Integer> supportedVersions = new HashMap<Integer, Integer>();
	
	private ArrayList<PeerConnection> connections; 	// players connections
	
	// maps players IDs with a connection associated with them
	// for local the connection is null
	private HashMap<Integer, PeerConnection> idMap = new HashMap<Integer, PeerConnection>();
	
	// connection which is currently served
	private PeerConnection currentConnection;
	private long startTime;
	private ArrayList<PeerConnection> acknowlegements; // IDs
	
	@Override
	protected ProtocolState join_game(String command) {
		try {
			//create JSON object of command
			join_game join_game = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);
			
			//reject if game in progress 
			if(engine != null) {
				System.out.println("Cant join Reason: Game in Progress!");
				errorMessage = "Reason: Game in Progress!";
				return ProtocolState.REJECT_JOIN_GAME;
			}
			
			//reject if no more space in game
			if(PlayerUtils.getPlayersInGame(state).size() >= maxNoOfPlayers) {
				System.out.println("Cant join Reason: Game is currentConnection.idFull!");
				// TODO: send over network
				errorMessage = "Reason: Game is Full!";
				return ProtocolState.REJECT_JOIN_GAME;
			}
			
			update(join_game.supported_features, supportedFeatures);
			update(join_game.supported_versions, supportedVersions);
			
		}
		catch(Exception error) {
			//if command string does not create a join_game object correctly
			System.out.println("The join_game command was malformed");
			return ProtocolState.REJECT_JOIN_GAME;
		}
		
		
		if(startingPlayers.size() < 3){
			System.out.println("STILL WAITING ON MORE PLAYERS");
			//send an accept join game to client
			return ProtocolState.ACCEPT_JOIN_GAME;
		}
		
		// TODO: change this -- wait for other people
		// WAIT HERE
		return ProtocolState.PING;	
	}
	



	@Override
	protected ProtocolState accept_join_game(String command) {
		//add player's name to players list
		accept_join_game accept_join_game =
				new accept_join_game(currentConnection.id, ack_timeout, move_timeout);
		
		int id = startingPlayers.size();
		
		// TODO: starting armies fix
		// TODO: add logic between host being a player
		startingPlayers.add(new Player(new RemotePlayer(), 0, id));
		
		idMap.put(id, currentConnection);
		
		//turn object into JSON string to be sent over network 
		String ajg = Jsonify.getObjectAsJsonString(accept_join_game);

		//sending to connection
		currentConnection.sendCommand(ajg);
		System.out.println("Sending... " + ajg);

		//if they had a name move to players joined
		return ProtocolState.PLAYERS_JOINED;
	}

	
	@Override
	protected ProtocolState reject_join_game(String reason) {
		//create json string 
		reject_join_game reject_join_game = new reject_join_game(errorMessage);		
		String rj = Jsonify.getObjectAsJsonString(reject_join_game);
		
		//send rejection 
		currentConnection.sendCommand(rj);
	
		System.out.println(rj);

		//end the game for this thread / player
		// TODO: think about it
		if(engine != null) return ProtocolState.END_GAME;
		//otherwise return to join game
		return ProtocolState.JOIN_GAME;
	}


	@Override
	protected ProtocolState players_joined(String command){
	
		players_joined players_joined = new players_joined(startingPlayers);
		String npj = Jsonify.getObjectAsJsonString(players_joined);
		System.out.println(npj);
		
		//send to new player containing current players
		sendToAll(npj);		
		
		return ProtocolState.JOIN_GAME;
	}


	@Override
	protected ProtocolState ping(String command){
		
		// add all players to the state - will be deleted later if needed
		state.setPlayers(startingPlayers);
		
		System.out.println("Number of players in game: " + startingPlayers.size());

		ping ping = new ping(startingPlayers.size(), 0);
		String p = Jsonify.getObjectAsJsonString(ping);
		//send ping to each client
		System.out.println(p);
		sendToAll(p);
		
		startTime = System.currentTimeMillis();
		return ProtocolState.PING_ACK;
	}
	
	@Override
	protected ProtocolState ping_ack(String command) {
		return acknowledge(command, true);
	}
	
	@Override
	protected ProtocolState receive_ack(String command){
		return acknowledge(command, false);
	}
	

	/**
	 * Receives acknowledgements (either ping or acknowledgement)
	 * it keeps receiving these until the timeout passes.
	 * 
	 * @param command
	 * @param isPing
	 * @return
	 */
	private ProtocolState acknowledge(String command, boolean isPing) {
		long time = System.currentTimeMillis();
		
		// if the time is not out receive the acknowledgement
		if((startTime - time)/1000 < ack_timeout){
			try{
				Object result;
				if(isPing)
					result = (PeerServer.protocol.setup.ping) Jsonify.getJsonStringAsObject(command, ping.class);
				else
					result = Jsonify.getJsonStringAsObject(command, acknowledgement.class);
				
				// if(result != null) ??
				acknowlegements.add(currentConnection);
			}
			catch(Exception error) {
				System.out.println("Wrong ping acknowledgement");
			}
			
			if(isPing)
				return ProtocolState.PING_ACK;
			else
				return ProtocolState.RECEIVE_ACK;
		}	
		
		
		else{
			for(PeerConnection c : connections){
				if(!acknowlegements.contains(c)){
					// removing players that have not sent acknowledgement
					if(!isPing){
						// message about timeout needs to be sent to all players
						timeout timeout = new timeout(0, 1, c.id);
						sendToAll(Jsonify.getObjectAsJsonString(timeout));
					}
					remove_player(c);
				}
			}
			
			if(startingPlayers.size() < 3){
				System.out.println("End of Game: Sending Leave_Game to all clients");
				return ProtocolState.LEAVE_GAME;
			}

			//if all clients respond 
			if(isPing)
				return ProtocolState.READY;
			else
				return ProtocolState.INIT_GAME;
		}
	}
	
	
	@Override
	protected ProtocolState timeout(String command){
		return protocolState;

	}
	
	@Override
	protected ProtocolState ready(String command){
		//send ready command to all connected clients ACK required
		ready ready = new ready(null, 0, 1);
		sendToAll(Jsonify.getObjectAsJsonString(ready));

		return ProtocolState.RECEIVE_ACK;
	}

	@Override
	protected ProtocolState init_game(String command){
		// finding the highest version supported by all
		int version = findHighestVersionSupportedByAll(supportedVersions);
		
		// get features that are supported by all
		ArrayList<String> features = findFeaturesSupportedByAll(supportedFeatures);
		
		initalise_game init_game = new initalise_game(version, (String[]) features.toArray());
		sendToAll(Jsonify.getObjectAsJsonString(init_game));
		
		
		return ProtocolState.READY;
	}

	@Override
	protected ProtocolState setup_game(String command){
		Object setup = Jsonify.getJsonStringAsObject(command, PeerServer.protocol.setup.setup.class);
		return protocolState;	
	}

	
	/**
	 * Removes the given connection from the list of supported connections
	 * and a player associated with it from the game state
	 * @param connection
	 */
	protected void remove_player(PeerConnection connection){
		int ID = -1;
		for(int id : idMap.keySet()){
			if(idMap.get(id) == connection)
				ID = id;
		}
		
		// remove that player from connections
		connections.remove(connection);
		
		if(ID != -1){
			idMap.remove(ID);
			// remove that player from the game state
			remove_player(ID);
		}
	}
	
	
	/**
	 * Sends a given command to all supported connections (all clients)
	 * @param command
	 */
	private void sendToAll(String command){
		for (PeerConnection c : connections) {
			c.sendCommand(command);
		}
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	private ArrayList<String> findFeaturesSupportedByAll(
			HashMap<String, Integer> map) {
		ArrayList<String> features = new ArrayList<String>();
		
		for(String a : map.keySet()){
			if(map.get(a) == connections.size())
				features.add(a);
		}	
		return features;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	private int findHighestVersionSupportedByAll(HashMap<Integer, Integer> map){
		int supported = 1;
		for(Integer a : map.keySet()){
			if(map.get(a) == connections.size())
				supported = a;
		}
		
		return supported;
	}

	/**
	 * Updates a given mapping based on the input array.
	 * Increases count of features / versions specified in the array.
	 * 
	 * @param input
	 * @param toUpdate
	 */
	private void update(Object[] input,	HashMap toUpdate) {
		Arrays.sort(input);
		Integer count;
		for(Object a : input){
			if(toUpdate.containsKey(a)){
				count = (Integer) toUpdate.get(a);
				toUpdate.put(a, count + 1);
			}
			else{
				toUpdate.put(toUpdate, 1);
			}
		}
	}

	public static void main(String[] args) {
		HostProtocol protocol = new HostProtocol();
		
		protocol.state = new State();
		RiskMapGameBuilder.addRiskTerritoriesToState(protocol.state);
		
		// null means that its a local player
		protocol.idMap.put(0, null);
		
		for (PeerConnection c : protocol.connections) {
			String playersLatestCommand = c.receiveCommand();
			if (!playersLatestCommand.equals("")) {
				protocol.currentConnection = c;
				protocol.handleCommand(playersLatestCommand);
			}		
		}

		// build the game state
		// accept players according to protocol
		//      as players connect break off the connections into threads
		//      create a mapping between 'remote player objects' and the threads
		//      add remote player objects to the game state
		//
		// add players to game state as
		//
		// start the game
		// field all received player actions and send them to appropriate player objects
		//
		//
	}
}