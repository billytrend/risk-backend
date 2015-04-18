package LobbyServer.LobbyState;

import GameState.Player;

import java.util.ArrayList;

public class UnstartedGameUtils {
    
    public void addPlayerToUnstartedGame(GameDescription g, Player p) {
        if (g.getPlayers().size() < g.getMaxPlayers()) {
            g.getPlayers().add(p);
        }
    }

    public void removePlayerFromUnstartedGame(GameDescription g, Player p) {
        g.getPlayers().remove(p);
    }

    public ArrayList<Player> getPlayersInGroup(ArrayList<GameDescription> groups) {
        ArrayList<Player> groupedPlayers = new ArrayList<Player>();
        for (GameDescription set : groups) {
            groupedPlayers.addAll(set.getPlayers());
        }
        return groupedPlayers;
    }

    public ArrayList<Player> getPlayersNotInGroup(ArrayList<GameDescription> groups, ArrayList<Player> a) {
        ArrayList<Player> groupedPlayers = getPlayersInGroup(groups);
        ArrayList<Player> allPlayers = new ArrayList<Player>(a);
        allPlayers.remove(groupedPlayers);
        return allPlayers;
    }
    

}
