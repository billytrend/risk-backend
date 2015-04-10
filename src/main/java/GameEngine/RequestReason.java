package GameEngine;

public enum RequestReason {
	PLACING_ARMIES_SET_UP,
	PLACING_REMAINING_ARMIES_PHASE, //placing armies during setup
	PLACING_ARMIES_PHASE, //placing armies throughout the game (reinforcement)
	ATTACK_CHOICE_FROM,
	ATTACK_CHOICE_TO,
	ATTACK_CHOICE_DICE,
	DEFEND_CHOICE_DICE,
	REINFORCEMENT_PHASE, // redeployment
	POST_ATTACK_MOVEMENT //how many armies to move onto concurred territory
}
