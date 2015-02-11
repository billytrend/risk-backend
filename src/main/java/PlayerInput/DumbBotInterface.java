package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import static com.esotericsoftware.minlog.Log.*;

/**
 * Used for a very simple AI acting as a player
 *
 */
public class DumbBotInterface implements PlayerInterface {

    Random ran = new Random();
	private static Scanner scanner;
	
    protected void emit(Player p, String message) {
        debug("[" + p.getId() + "]" + "\t\t");
        debug(message);
    }

    /**
     * Getting an integer from the user
     * @return
     */
    protected static int easyIn() {
        // ADDITIONAL CHECKS?
        int a;
        debug("Please enter your selection: ");
        scanner = new Scanner(System.in);
		a = scanner.nextInt();
        return a;
    }
    

    /**
     * 
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason) {
        emit(player, " how many dice do you want to throw? Max " + max);
        emit(player, "Chose " + max);
        return max;
    }

    /**
     * 
     */
    public Territory getTerritory(Player player, HashSet<Territory> possibles,
                                         boolean canResign, RequestReason reason) {
        ArrayList<Territory> posList = new ArrayList<Territory>(possibles);
        emit(player, "Please choose a territory");
       
        // the player can decide not to make a choice
        // in case of starting an attack or moving armies
        if(canResign){
        	emit(player, "\t0. Don't choose");
        }

        for(int i = 0; i < possibles.size(); i++) {
            emit(player,  "\t" + (i + 1) + ". " + posList.get(i).getId());
        }
        
        // random choice
        Integer choice = canResign ? ran.nextInt(posList.size() + 1) : 
        				(ran.nextInt(posList.size()) + 1);
        
        emit(player, "Chose " + choice);
        return  (choice == 0) ? null :
        				posList.get(choice - 1);

    }
    
    /**
     * 
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason) {
        emit(player, "How many armies would you like to move? Max " + max);
        emit(player, "Chose " + max);
        return ran.nextInt(max + 1);
    }

    @Override
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        return null;
    }

}
