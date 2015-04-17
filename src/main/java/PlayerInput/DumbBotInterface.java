package PlayerInput;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameUtils.Results.Change;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * Used for a very simple AI acting as a player
 *
 */
public class DumbBotInterface implements PlayerInterface {

    Random ran = new Random();
	private static Scanner scanner;
	
    protected void emit(Player p, String message) {
//        debug("[" + p.getId() + "]" + "\t\t");
//        debug(message);
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
    public int getNumberOfDice(Player currentPlayer, int max, RequestReason attackChoiceDice, Territory attacking, Territory defending) {
        emit(currentPlayer, " how many dice do you want to throw? Max " + max);
        emit(currentPlayer, "Chose " + max);
        return max;
    }

    /**
     * 
     */
    public Territory getTerritory(Player player, HashSet<Territory> possibles,Territory from,
                                         boolean canResign, RequestReason reason) {
    	
        ArrayList<Territory> posList = new ArrayList<Territory>(possibles);

        String out = "Please choose a territory";
//        switch(reason){
//        case ATTACK_CHOICE:
//        	out += " to attack";
//        	break;
//        default: 
//        	out += " to place an army on";
//
//        }
        emit(player, out);
	
       
        // the player can decide not to make a choice
        // in case of starting an attack or moving armies
        if(canResign){
        	emit(player, "\t0. Don't choose");
        }

        for(int i = 0; i < possibles.size(); i++) {
            emit(player,  "\t" + (i + 1) + ". " + posList.get(i).getId());
        }
        
        // random choice
        if (ran.nextInt(10) == 0 && canResign) {
            return null;
        }

        return posList.get(ran.nextInt(posList.size()));

    }
    
    /**
     * 
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        emit(player, "How many armies would you like to move? Max " + max);
        emit(player, "Chose " + max);


        return ran.nextInt(max + 1);
    }

    @Override
    public Triplet<Card, Card, Card> getCardChoice(Player player, ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
        return possibleCombinations.get(0);
    }

    @Override
    public void reportStateChange(Change change) {

    }

}
