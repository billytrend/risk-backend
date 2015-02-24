package LobbyServer.LobbyState.ObjectFromClient.GameComms;

import GameState.Territory;

import java.util.HashSet;

public class TerritoryRequest extends Request {
    public final String requestType = "territory";
    public HashSet<Territory> possibles;
	public boolean canResign;
}
