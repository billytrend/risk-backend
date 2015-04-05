package ProtocolTests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import GeneralUtils.Jsonify;
import PeerServer.protocol.setup.accept_join_game;
import PeerServer.protocol.setup.join_game;
import PeerServer.protocol.setup.reject_join_game;

public class ProtocolTest {

	@Test
	public void rejectJoinGameTest() {
		//create valid command
		String command = "{\"command\":\"reject_join_game\",\"payload\":\"Game in progress\"}";
		//crate object from the JSON string
		reject_join_game rjg = new Gson().fromJson(command, reject_join_game.class);
		//check it is not null
		assertNotNull(rjg);  
		//check each field is valid
		assertEquals(rjg.command, "reject_join_game");
		System.out.println(rjg.command);
		assertEquals(rjg.payload, "Game in progress");
		System.out.println(rjg.payload);
		
		//create a new object 
		reject_join_game rjgToString = new reject_join_game("Game in progress");
		String rjgString = new Gson().toJson(rjgToString);
		System.out.println(rjgString);
		assertEquals(command, rjgString);		
}
	
	
	@Test
	public void joinGameTest() {
		//create valid command
		String command = "{\"command\":\"join_game\",\"payload\":"
				+ "{\"supported_versions\":[1],"
				+ "\"supported_features\":[\"custom_map\"],\"name\":\"Player 1\"}}";
		//crate object from the JSON string
		join_game jg = new Gson().fromJson(command, join_game.class);
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
	public void acceptJoinGameTest() {
		//create valid command
		String command = "{\"command\":\"accept_join_game\",\"payload\":"
				+ "{\"player_id\":1,\"acknowledgement_timeout\":2,\"move_timeout\":30}}";
		//crate object from the JSON string
		accept_join_game ajg = new Gson().fromJson(command, accept_join_game.class);
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
		String ajgString = new Gson().toJson(ajgToString);
		System.out.println(ajgString);
		assertEquals(command, ajgString);
		
		
		
	}
	
}
