package PeerServer.server;

/**
 * Enum representing all possible states of the 
 * networked game.
 *
 */
public enum ProtocolState {
	JOIN_GAME,
	PLAYERS_JOINED,
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
	FORTIFY,
}
