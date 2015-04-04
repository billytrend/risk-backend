package PeerServer.server;

/**
 * Enum representing all possible states of the 
 * networked game.
 *
 */
public enum ProtocolState {
	WAITING_FOR_PLAYERS,
	JOIN_GAME,
	PLAYERS_JOINED,
	ACK,
	PING,
	PING_ACK, 
	READY,
	RECEIVE_ACK, 
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
