package ProtocolTests;

import static org.junit.Assert.*;

import org.junit.Test;

import PeerServer.protocol.setup.reject_join_game;

public class ProtocolTest {

	@Test
	public void messageFormatTest() {
		String command = "\"message\": \"{ \"command\": \"reject_join_game\", "
				+ "\"payload\":\"Game in progress\"";
		reject_join_game rjg = new reject_join_game(command);
		System.out.println(rjg);
		
		
	}
}
