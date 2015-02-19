package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameState.Territory;

import java.util.HashSet;

public class TerritoryRequest extends Request {
	public HashSet<Territory> possibles;
	public boolean canResign;
}
