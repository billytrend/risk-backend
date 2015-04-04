package ProtocolTests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;

import GeneralUtils.Jsonify;
import PeerServer.protocol.setup.reject_join_game;

public class ProtocolTest {

	@Test
	public void rejectJoinGameTest() {
		String command = "{\"command\":\"reject_join_game\", \"payload\":\"Game in progress\"}";
				
		reject_join_game rjg = new Gson().fromJson(command, reject_join_game.class);
		assertNotNull(rjg);  
		
		//make sure object created is not null
		assertNotNull(rjg);
		//check each field is valid
		assertEquals(rjg.command, "reject_join_game");
		System.out.println(rjg.command);
		assertEquals(rjg.payload, "Game in progress");
	}
}
