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

/**
 * Used for a very simple AI acting as a player
 *
 */
public class CommandLineInterface implements PlayerInterface {

    Random ran = new Random();
	private static Scanner scanner;
	
    protected void emit(Player p, String message) {
        System.out.print("[" + p.getId() + "]" + "\t\t");
        System.out.println(message);
    }

    /**
     * Getting an integer from the user
     * @return
     */
    protected static int easyIn() {
        // ADDITIONAL CHECKS?
        int a;
        System.out.print("Please enter your selection: ");
        scanner = new Scanner(System.in);
		a = scanner.nextInt();
        return a;
    }
    

    /**
     * 
     */
    public int getNumberOfDice(Player player, int max, RequestReason reason, Territory attacking, Territory defending) {
        emit(player, "How many dice would you like to throw? Max " + max);
        int dice = easyIn();
        if(dice > max){
        	emit(player, "You don't have that many dice.");
        	return getNumberOfDice(player, max, reason, attacking, defending);
        }

        else if(dice < 1){
        	emit(player, "You have to throw at least 1 dice.");
        	return getNumberOfDice(player, max, reason, attacking, defending);
        }

        return max;
    }

    /**
     * 
     */
    public Territory getTerritory(Player player, HashSet<Territory> possibles, Territory from, boolean canResign, RequestReason reason) {
        ArrayList<Territory> posList = new ArrayList<Territory>(possibles);
        emit(player, reason.toString());
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
        
        int choice = easyIn();
        
        return  (choice == 0) ? null :
        				posList.get(choice - 1);

    }
    
    /**
     * 
     */
    public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
        emit(player, "How many armies would you like to move? Max " + max);
        int armies = easyIn();
        if(armies > max){
        	emit(player, "You don't have that many armies.");
        	getNumberOfArmies(player, max, reason, null,null);
        }
        else if(armies < 0){
        	emit(player, "Give a positive integer.");
        	getNumberOfArmies(player, max, reason, null, null);
        }
        return armies;
    }

    /**
     * 
     */
    public void giveCard(Player player, Card card) {
        return;
    }

    /**
     * 
     */
    public Card getCardOptions() {
        return null;
    }

	@Override
	public Triplet<Card, Card, Card> getCardChoice(Player player,
			ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void reportStateChange(Change change) {

    }

}

