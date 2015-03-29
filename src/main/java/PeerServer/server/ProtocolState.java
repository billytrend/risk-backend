package PeerServer.server;

/**
 * Enum representing all possible states of the 
 * networked game.
 *
 */
public enum ProtocolState {
	
	JOIN_GAME,
	REJECT_JOIN_GAME,
	ACCEPT_JOIN_GAME,
	PLAYERS_JOINED,
	ACK,
	PING,
	READY,
	INIT_GAME,
	SETUP_GAME,	
	PLAY_CARDS,
	DRAW_CARD,
	DEPLOY,
	ORDER_OF_TURNS,
	SHUFFLE_DECK,
	INIT_TERRITORIES,
	REINFORCING_TERRITORIES,
	START_GAME,
	RECIEVE_ARMIES,
	PLACE_ARMIES,
	ATTACK,
	DEFEND,
	ATTACK_CAPTURE,
	TRADING_IN_CARDS,
	DRAWING_CARDS,
	FORTIFYING, 		//unsure if necessary due to reinforcing terrs 
	LEAVE_GAME,
	TIMEOUT,
	END_GAME,	
}
