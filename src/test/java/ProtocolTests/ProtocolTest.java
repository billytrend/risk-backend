package ProtocolTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import GameState.Player;
import GeneralUtils.Jsonify;
import PeerServer.protocol.gameplay.setup;
import PeerServer.protocol.setup.accept_join_game;
import PeerServer.protocol.setup.initialise_game;
import PeerServer.protocol.setup.join_game;
import PeerServer.protocol.setup.ping;
import PeerServer.protocol.setup.players_joined;
import PeerServer.protocol.setup.ready;
import PeerServer.protocol.setup.reject_join_game;

public class ProtocolTest {
	
	@Test
	public void joinGameTest() {
		//create valid command
		String command = "{\"command\":\"join_game\",\"payload\":"
				+ "{\"supported_versions\":[1],"
				+ "\"supported_features\":[\"custom_map\"],\"name\":\"Player 1\"}}";
		//crate object from the JSON string
		join_game jg = (join_game) Jsonify.getJsonStringAsObject(command, join_game.class);
		//check it is not null
		assertNotNull(jg);
		
		//check each field is valid
		assertEquals(jg.command, "join_game");
		System.out.println(jg.command);
		assertEquals(jg.payload.supported_versions[0].toString(), "1");
		assertEquals(jg.payload.supported_features[0].toString(), "custom_map");
		assertEquals(jg.payload.name, "Player 1");
		
		Integer [] supported_versions = {1};
		String[] supported_features = {"custom_map"};
		String name = "Player 1";
		
		//create a new object 
		join_game rjgToString = new join_game(supported_versions, supported_features, name);
		String rjgString = new Gson().toJson(rjgToString);
		System.out.println(rjgString);
		assertEquals(command, rjgString);	
		
	}

	@Test
	public void rejectJoinGameTest() {
		//create valid command
		String command = "{\"command\":\"reject_join_game\",\"payload\":\"Game in progress\"}";
		//crate object from the JSON string
		reject_join_game rjg = (reject_join_game) Jsonify.getJsonStringAsObject(command, reject_join_game.class);
		//check it is not null
		assertNotNull(rjg);  
		//check each field is valid
		assertEquals(rjg.command, "reject_join_game");
		System.out.println(rjg.command);
		assertEquals(rjg.payload, "Game in progress");
		System.out.println(rjg.payload);
		
		//create a new object 
		reject_join_game rjgToString = new reject_join_game("Game in progress");
		String rjgString = Jsonify.getObjectAsJsonString(rjgToString);
		System.out.println(rjgString);
		assertEquals(command, rjgString);		
	}
	
	@Test
	public void acceptJoinGameTest() {
		//create valid command
		String command = "{\"command\":\"accept_join_game\",\"payload\":"
				+ "{\"player_id\":1,\"acknowledgement_timeout\":2,\"move_timeout\":30}}";
		//crate object from the JSON string
		accept_join_game ajg = (accept_join_game) Jsonify.getJsonStringAsObject(command, accept_join_game.class);
		//check it is not null
		assertNotNull(ajg);
		
		//check each field is valid
		assertEquals(ajg.command, "accept_join_game");
		System.out.println(ajg.command);
		assertEquals(ajg.payload.player_id, 1);
		assertEquals(ajg.payload.acknowledgement_timeout, 2);
		assertEquals(ajg.payload.move_timeout, 30);
		
		int player_id = 1;
		int acknowledgement_timeout = 2;
		int move_timeout = 30;
		
		//create a new object 
		accept_join_game ajgToString = new accept_join_game(player_id, acknowledgement_timeout, move_timeout);
		String ajgString = Jsonify.getObjectAsJsonString(ajgToString);
		System.out.println(ajgString);
		assertEquals(command, ajgString);	
	}
	
	
	@Test
	public void playersJoinedTest(){
		String command = "{\"command\":\"players_joined\",\"payload\": "
				+ "[[0, \"Player A\", \"\"],[1, \"Player B\", \"\"]]}";
		
		players_joined pj = (players_joined) Jsonify.getJsonStringAsObject(command, players_joined.class);
		assertNotNull(pj);
		
		//check each field is valid
		assertEquals(pj.command, "players_joined");
		
		//check size is right
		assertEquals(2, pj.payload.length);
		
		//create a copy of inputted rows 
		Object[] row1 = {0, "Player A", ""};
		Object[] row2 = {1, "Player B", ""};
		
		//check the values are right
		assertEquals(row1[0], ((Double)pj.payload[0][0]).intValue());
		assertEquals(row1[1], pj.payload[0][1]);
		assertEquals(row1[2], pj.payload[0][2]);

		assertEquals(row2[0], ((Double)pj.payload[1][0]).intValue());
		assertEquals(row2[1], pj.payload[1][1]);
		assertEquals(row2[2], pj.payload[1][2]);

		
		//assertTrue(Arrays.deepEquals(row1, pj.payload[0]));
		//assertTrue(Arrays.deepEquals(row2, pj.payload[1]));
		
		ArrayList<Player> playersArray = new ArrayList<Player>();
		Player A = new Player(null, 0, "Player A");
		Player B = new Player(null, 1, "Player B");
		A.setPublicKey("");
		B.setPublicKey("");
		playersArray.add(A);
		playersArray.add(B);
		
		//create a new object 
		players_joined pjToString = new players_joined(playersArray);
		String pjString = Jsonify.getObjectAsJsonString(pjToString);
		System.out.println(pjString);
		command = command.replaceAll(",\\s",",");
		command = command .replaceAll(":\\s",":");
		assertEquals(command, pjString);	
	}

	@Test
	public void pingTest(){
		String command = "{\"command\":\"ping\",\"payload\":5,\"player_id\":0}";
		
		ping ping = (PeerServer.protocol.setup.ping) Jsonify.getJsonStringAsObject(command, ping.class);
		assertNotNull(ping);
		
		//check each field is valid
		assertEquals(ping.command, "ping");
		assertEquals(ping.payload.intValue(), 5);
		assertEquals(ping.player_id, 0);
		
		//create a new object 
		ping pingToString = new ping(5, 0);
		String pingString = Jsonify.getObjectAsJsonString(pingToString);
		System.out.println(pingString);
		command = command.replaceAll(",\\s",",");
		command = command .replaceAll(":\\s",":");
		assertEquals(command, pingString);	
	}

	
	@Test
	public void readyTest(){
		String command = "{\"command\": \"ready\","
				+ "\"payload\": null,\"player_id\": 0,\"ack_id\": 1}";
		
		ready ready = (PeerServer.protocol.setup.ready) Jsonify.getJsonStringAsObject(command, ready.class);
		//checks its not null
		assertNotNull(ready);
		
		//check each field is valid
		assertEquals(ready.command, "ready");
		assertEquals(ready.player_id, 0);
		assertEquals(ready.payload, null);
		assertEquals(ready.ack_id, 1);
		
		//create a new object 
		ready readyToString = new ready(null, 0, 1);
		String readyString = Jsonify.getObjectAsJsonString(readyToString);
		System.out.println(readyString);
		command = command.replaceAll(",\\s",",");
		command = command .replaceAll(":\\s",":");
		assertEquals(command, readyString);	
	}
	
	@Test
	public void initaliseGameTest(){
		String command = "{\"command\":\"initialise_game\","
				+ "\"payload\":{\"version\":1,\"supported_features\":[\"custom_map\"]}}";
		
		initialise_game ig = (initialise_game) Jsonify.getJsonStringAsObject(command, initialise_game.class);
		//checks its not null
		assertNotNull(ig);
		
		//check each field is valid
		assertEquals(ig.command, "initialise_game");
		assertEquals(ig.payload.version.intValue(), 1);
		assertEquals(ig.payload.supported_features[0], "custom_map");
		
		String[] supportedFeatures = {"custom_map"};
		
		//create a new object 
		initialise_game initGame = new initialise_game(1, supportedFeatures);
		String initGameString = Jsonify.getObjectAsJsonString(initGame);
		System.out.println(initGameString);
		assertEquals(command, initGameString);	
	}
	
	
	//******************** not yet implemented 
	@Test
	public void setupTest(){
		String command = "{\"command\": \"setup\",\"payload\": 5,\"player_id\": 0,\"ack_id\": 1}";
		
		setup setup = (PeerServer.protocol.gameplay.setup) Jsonify.getJsonStringAsObject(command, setup.class);
		//check it is not null
		assertNotNull(setup);
		
		//check each field is valid
		assertEquals(setup.command, "setup");
		assertEquals(setup.payload, 5);
		assertEquals(setup.player_id, 0);
		assertEquals(setup.ack_id, 1);

		//create a new object 
		setup setupToString = new setup("setup", 5, 0, 1);
		String setupString = Jsonify.getObjectAsJsonString(setupToString);
		System.out.println(setupString);
		command = command.replaceAll(",\\s",",");
		command = command .replaceAll(":\\s",":");
		assertEquals(command, setupString);
		
	}
	
}
