package PeerServer.server;

/**
 * Enum representing all possible states of the 
 * networked game.
 *
 */
public enum ProtocolState {
	JOIN_GAME,
	JOIN_RESPONSE,
	PLAYERS_JOINED,
	PING,
	PING_ACK, 
	READY,
	ACK,
	INIT_GAME,
	SETUP_GAME,	
	PLAY_CARDS,
	DRAW_CARD,
	DEPLOY,
	ATTACK,
	DEFEND,
	ATTACK_CAPTURE,
	TRADING_IN_CARDS,
	DRAWING_CARDS,
	ROLL, 
	FORTIFY,
	LEAVE_GAME,
	TIMEOUT,
	END_GAME, ACCEPT_JOIN_GAME, REJECT_JOIN_GAME,
}
