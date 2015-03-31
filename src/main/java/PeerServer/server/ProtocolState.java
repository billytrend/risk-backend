package PeerServer.server;

/**
 * Enum representing all possible states of the 
 * networked game.
 *
 */
public enum ProtocolState {
	WAITING_FOR_PLAYERS,
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
	ATTACK,
	DEFEND,
	ATTACK_CAPTURE,
	TRADING_IN_CARDS,
	DRAWING_CARDS,
	ROLL, 
	ROLL_HASH, 
	ROLL_NUMBER,
	FORTIFY,
	LEAVE_GAME,
	TIMEOUT,
	END_GAME, 	
}